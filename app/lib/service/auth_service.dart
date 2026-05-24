import 'dart:convert';
import 'dart:math';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;

import 'package:crypto/crypto.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

import '../config/app_config.dart';

class AuthService {
  static const _keyAccessToken = 'access_token';
  static const _keyRefreshToken = 'refresh_token';
  static const _keyIdToken = 'id_token';
  static const _keyCodeVerifier = 'pkce_code_verifier';
  static const _keyState = 'oauth_state';

  /// Guards against concurrent refresh calls. If a refresh is already
  /// in-flight, subsequent callers await the same Future instead of
  /// starting another token-endpoint request (which would invalidate the
  /// just-issued refresh token due to Spring's token rotation).
  Future<bool>? _refreshFuture;

  String _generateCodeVerifier() {
    final random = Random.secure();
    final bytes = List<int>.generate(32, (_) => random.nextInt(256));
    return base64UrlEncode(bytes).replaceAll('=', '');
  }

  String _generateCodeChallenge(String verifier) {
    final bytes = utf8.encode(verifier);
    final digest = sha256.convert(bytes);
    return base64UrlEncode(digest.bytes).replaceAll('=', '');
  }

  Future<void> login() async {
    final prefs = await SharedPreferences.getInstance();
    final codeVerifier = _generateCodeVerifier();
    final state = _generateCodeVerifier();

    await prefs.setString(_keyCodeVerifier, codeVerifier);
    await prefs.setString(_keyState, state);

    final authUrl = Uri.parse(AppConfig.authorizationEndpoint)
        .replace(queryParameters: {
      'response_type': 'code',
      'client_id': AppConfig.clientId,
      'redirect_uri': AppConfig.redirectUri,
      'scope': AppConfig.scopes.join(' '),
      'code_challenge': _generateCodeChallenge(codeVerifier),
      'code_challenge_method': 'S256',
      'state': state,
    }).toString();

    html.window.location.assign(authUrl);
  }

  Future<bool> handleCallback(String code, String state) async {
    final prefs = await SharedPreferences.getInstance();
    final storedState = prefs.getString(_keyState);
    final codeVerifier = prefs.getString(_keyCodeVerifier);

    if (storedState == null || storedState != state || codeVerifier == null) {
      return false;
    }

    final response = await http.post(
      Uri.parse(AppConfig.tokenEndpoint),
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body: {
        'grant_type': 'authorization_code',
        'code': code,
        'redirect_uri': AppConfig.redirectUri,
        'client_id': AppConfig.clientId,
        'code_verifier': codeVerifier,
      },
    );

    if (response.statusCode != 200) {
      return false;
    }

    final data = jsonDecode(response.body) as Map<String, dynamic>;
    await prefs.setString(_keyAccessToken, data['access_token'] as String);
    final refreshToken = data['refresh_token'];
    if (refreshToken != null) {
      await prefs.setString(_keyRefreshToken, refreshToken as String);
    }
    final idToken = data['id_token'];
    if (idToken != null) {
      await prefs.setString(_keyIdToken, idToken as String);
    }

    await prefs.remove(_keyCodeVerifier);
    await prefs.remove(_keyState);

    // Clean up ?code=...&state=... from URL
    html.window.history.pushState(null, '', '/');

    return true;
  }

  Future<bool> refreshAccessToken() {
    // If a refresh is already running, return the same Future so concurrent
    // callers don't fire a second token-endpoint request.
    _refreshFuture ??= _doRefresh().whenComplete(() => _refreshFuture = null);
    return _refreshFuture!;
  }

  Future<bool> _doRefresh() async {
    final prefs = await SharedPreferences.getInstance();
    final refreshToken = prefs.getString(_keyRefreshToken);
    if (refreshToken == null) return false;

    try {
      final response = await http.post(
        Uri.parse(AppConfig.tokenEndpoint),
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: {
          'grant_type': 'refresh_token',
          'refresh_token': refreshToken,
          'client_id': AppConfig.clientId,
        },
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        await prefs.setString(_keyAccessToken, data['access_token'] as String);
        if (data['refresh_token'] != null) {
          await prefs.setString(
              _keyRefreshToken, data['refresh_token'] as String);
        }
        return true;
      }

      // 400/401 from the token endpoint means the refresh token is invalid
      // or expired — the session is unrecoverable, so log out.
      if (response.statusCode == 400 || response.statusCode == 401) {
        await logout();
      }
      // 5xx or other transient errors: don't log out, let the caller handle.
      return false;
    } catch (_) {
      // Network error — don't log out, the user may just be offline.
      return false;
    }
  }

  /// Decodes the JWT payload and returns the `exp` field as a [DateTime],
  /// or null if the token is malformed or has no `exp` claim.
  DateTime? _tokenExpiry(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return null;
      // Base64Url-decode the payload section, adding padding as needed.
      var payload = parts[1];
      final rem = payload.length % 4;
      if (rem == 2) payload += '==';
      if (rem == 3) payload += '=';
      final decoded = utf8.decode(base64Url.decode(payload));
      final exp = (jsonDecode(decoded) as Map<String, dynamic>)['exp'];
      if (exp == null) return null;
      return DateTime.fromMillisecondsSinceEpoch((exp as int) * 1000);
    } catch (_) {
      return null;
    }
  }

  /// Returns true when [token] is already expired or expires within [buffer].
  bool _isExpiring(String token, {Duration buffer = const Duration(seconds: 30)}) {
    final expiry = _tokenExpiry(token);
    if (expiry == null) return false; // can't determine → assume valid
    return DateTime.now().isAfter(expiry.subtract(buffer));
  }

  /// Returns a valid access token.
  ///
  /// - If the stored token is still fresh → returns it immediately.
  /// - If it is expiring/expired → attempts a silent refresh first.
  ///   • Refresh succeeds → returns the new token.
  ///   • Refresh fails transiently (network / 5xx) → returns the OLD
  ///     (possibly expired) token so the caller can still attempt the
  ///     request; a 401 from the API will trigger another refresh attempt
  ///     via [http_client]'s reactive handler.
  ///   • Refresh fails definitively (400/401 → session is dead) → [logout]
  ///     was already called inside [_doRefresh]; return null so callers
  ///     skip the request.
  Future<String?> getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(_keyAccessToken);
    if (token == null || token.isEmpty) return null;

    if (!_isExpiring(token)) return token;

    // Token is expiring/expired — attempt a silent refresh.
    final ok = await refreshAccessToken();
    if (ok) {
      // Return the freshly stored token.
      return prefs.getString(_keyAccessToken) ?? token;
    }

    // Refresh failed transiently: return the old token and let the
    // reactive 401 handler in AuthenticatedHttpClient deal with it.
    // If _doRefresh called logout() (definitive failure), the stored
    // access token was already removed, so getString returns null here.
    return prefs.getString(_keyAccessToken);
  }

  /// Returns true when the user has an active session stored locally.
  ///
  /// Does NOT make any network calls — routing decisions must be instant.
  /// The actual token validity is verified lazily on the first API call.
  ///
  /// Rules:
  /// • Refresh token present → can always obtain a new access token → authenticated.
  /// • No refresh token but unexpired access token → still authenticated.
  /// • Expired access token with no refresh token → must re-login.
  Future<bool> isAuthenticated() async {
    final prefs = await SharedPreferences.getInstance();

    final refreshToken = prefs.getString(_keyRefreshToken);
    if (refreshToken != null && refreshToken.isNotEmpty) return true;

    final accessToken = prefs.getString(_keyAccessToken);
    if (accessToken == null || accessToken.isEmpty) return false;
    // No refresh token: only allow if the access token has not yet expired.
    return !_isExpiring(accessToken, buffer: Duration.zero);
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    final idToken = prefs.getString(_keyIdToken);

    await prefs.remove(_keyAccessToken);
    await prefs.remove(_keyRefreshToken);
    await prefs.remove(_keyIdToken);

    // OIDC RP-Initiated Logout: redirect to the auth server's /connect/logout.
    // Passing id_token_hint lets the server identify the client and skip the
    // confirmation page, then redirect straight to post_logout_redirect_uri.
    // After the server clears its session cookie, the browser lands back on
    // AppConfig.redirectUri → AppRouter finds no tokens → navigates to /login.
    final params = <String, String>{
      'post_logout_redirect_uri': AppConfig.redirectUri,
    };
    if (idToken != null) {
      params['id_token_hint'] = idToken;
    }

    final logoutUrl = Uri.parse(AppConfig.logoutEndpoint)
        .replace(queryParameters: params)
        .toString();

    html.window.location.assign(logoutUrl);
  }
}
