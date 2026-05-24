import 'dart:convert';

import 'package:http/http.dart' as http;

import 'auth_service.dart';

/// Base class for all API services.
/// Provides authenticated HTTP helpers with automatic 401 → token-refresh retry.
abstract class AuthenticatedHttpClient {
  final AuthService authService;

  const AuthenticatedHttpClient(this.authService);

  Future<Map<String, String>?> _headers() async {
    final token = await authService.getAccessToken();
    if (token == null) return null;
    return {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };
  }

  Future<http.Response?> get(String url) async {
    final headers = await _headers();
    if (headers == null) return null;
    var res = await http.get(Uri.parse(url), headers: headers);
    if (res.statusCode == 401) {
      if (!await authService.refreshAccessToken()) return null;
      res = await http.get(Uri.parse(url), headers: await _headers() ?? {});
    }
    return res;
  }

  Future<http.Response?> post(String url, Map<String, dynamic> body) async {
    final headers = await _headers();
    if (headers == null) return null;
    var res = await http.post(Uri.parse(url),
        headers: headers, body: jsonEncode(body));
    if (res.statusCode == 401) {
      if (!await authService.refreshAccessToken()) return null;
      res = await http.post(Uri.parse(url),
          headers: await _headers() ?? {}, body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response?> put(String url, Map<String, dynamic> body) async {
    final headers = await _headers();
    if (headers == null) return null;
    var res = await http.put(Uri.parse(url),
        headers: headers, body: jsonEncode(body));
    if (res.statusCode == 401) {
      if (!await authService.refreshAccessToken()) return null;
      res = await http.put(Uri.parse(url),
          headers: await _headers() ?? {}, body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response?> patch(String url, Map<String, dynamic> body) async {
    final headers = await _headers();
    if (headers == null) return null;
    var res = await http.patch(Uri.parse(url),
        headers: headers, body: jsonEncode(body));
    if (res.statusCode == 401) {
      if (!await authService.refreshAccessToken()) return null;
      res = await http.patch(Uri.parse(url),
          headers: await _headers() ?? {}, body: jsonEncode(body));
    }
    return res;
  }

  Future<http.Response?> delete(String url) async {
    final headers = await _headers();
    if (headers == null) return null;
    var res = await http.delete(Uri.parse(url), headers: headers);
    if (res.statusCode == 401) {
      if (!await authService.refreshAccessToken()) return null;
      res =
          await http.delete(Uri.parse(url), headers: await _headers() ?? {});
    }
    return res;
  }
}
