import 'dart:convert';

import 'package:http/http.dart' as http;

import 'auth_service.dart';

/// A public API route: an HTTP method + a path pattern, matched against
/// [Uri.path] (gateway prefix included, query string ignored).
class _PublicRoute {
  final String method;
  final RegExp pathPattern;

  const _PublicRoute(this.method, this.pathPattern);

  bool matches(String method, String path) =>
      method == this.method && pathPattern.hasMatch(path);
}

/// Routes that must never receive an `Authorization` header, regardless of
/// whether a (possibly stale/invalid) token is available locally.
///
/// This mirrors the backend's own `permitAll()` routes (see vacancy-app's
/// `VacancyAppConfiguration`). It matters because the OAuth2 resource server
/// validates ANY bearer token it's handed — even on a public route — and
/// rejects the request with 401 if that token is invalid or expired.
/// Checking "is the user logged in" isn't enough: a locally-stored token can
/// look unexpired to the client (see [AuthService.getAccessToken]) while
/// being rejected server-side (e.g. after the auth server rotates its
/// signing key), which would otherwise block guest access entirely.
final List<_PublicRoute> _publicRoutes = [
  _PublicRoute('GET', RegExp(r'^/vacancy-app/api/v1/vacancies$')),
  // Vacancy detail by id, but not "/vacancies/saved" (stays authenticated).
  _PublicRoute(
      'GET', RegExp(r'^/vacancy-app/api/v1/vacancies/(?!saved$)[^/]+$')),
];

bool _isPublicRoute(String method, String url) {
  final path = Uri.parse(url).path;
  return _publicRoutes.any((r) => r.matches(method, path));
}

/// Base class for all API services.
/// Provides authenticated HTTP helpers with automatic 401 → token-refresh retry.
abstract class AuthenticatedHttpClient {
  final AuthService authService;

  const AuthenticatedHttpClient(this.authService);

  /// Omits the token entirely for [_publicRoutes]; otherwise attaches one
  /// when a session exists.
  Future<Map<String, String>> _headers(bool isPublic) async {
    final headers = {'Content-Type': 'application/json'};
    if (isPublic) return headers;

    final token = await authService.getAccessToken();
    if (token != null) headers['Authorization'] = 'Bearer $token';
    return headers;
  }

  Future<http.Response> get(String url) async {
    final isPublic = _isPublicRoute('GET', url);
    var res = await http.get(Uri.parse(url), headers: await _headers(isPublic));
    if (!isPublic && res.statusCode == 401 && await authService.refreshAccessToken()) {
      res = await http.get(Uri.parse(url), headers: await _headers(isPublic));
    }
    return res;
  }

  Future<http.Response> post(String url, Map<String, dynamic> body) async {
    final isPublic = _isPublicRoute('POST', url);
    var res = await http.post(Uri.parse(url),
        headers: await _headers(isPublic), body: jsonEncode(body));
    if (!isPublic && res.statusCode == 401 && await authService.refreshAccessToken()) {
      res = await http.post(Uri.parse(url),
          headers: await _headers(isPublic), body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response> put(String url, Map<String, dynamic> body) async {
    final isPublic = _isPublicRoute('PUT', url);
    var res = await http.put(Uri.parse(url),
        headers: await _headers(isPublic), body: jsonEncode(body));
    if (!isPublic && res.statusCode == 401 && await authService.refreshAccessToken()) {
      res = await http.put(Uri.parse(url),
          headers: await _headers(isPublic), body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response> patch(String url, Map<String, dynamic> body) async {
    final isPublic = _isPublicRoute('PATCH', url);
    var res = await http.patch(Uri.parse(url),
        headers: await _headers(isPublic), body: jsonEncode(body));
    if (!isPublic && res.statusCode == 401 && await authService.refreshAccessToken()) {
      res = await http.patch(Uri.parse(url),
          headers: await _headers(isPublic), body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response> delete(String url) async {
    final isPublic = _isPublicRoute('DELETE', url);
    var res = await http.delete(Uri.parse(url), headers: await _headers(isPublic));
    if (!isPublic && res.statusCode == 401 && await authService.refreshAccessToken()) {
      res = await http.delete(Uri.parse(url), headers: await _headers(isPublic));
    }
    return res;
  }
}
