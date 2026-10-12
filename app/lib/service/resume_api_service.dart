import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../config/app_config.dart';
import '../model/resume.dart';
import 'http_client.dart';

class ResumeApiService extends AuthenticatedHttpClient {
  ResumeApiService(super.authService);

  Future<List<Resume>> findAll() async {
    final response = await get('${AppConfig.resumesEndpoint}/my');
    if (response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => Resume.fromJson(e as Map<String, dynamic>)).toList();
  }

  /// Recruiter/company-founder only — resumes belonging to another account
  /// (e.g. an applicant found via user search). Returns an empty list on
  /// 403 (caller isn't a recruiter/company founder) as well as on any other
  /// failure, since the caller only needs to distinguish "has a resume" from
  /// "doesn't".
  Future<List<Resume>> findAllByAccountId(String accountId) async {
    final url = Uri.parse(AppConfig.resumesEndpoint)
        .replace(queryParameters: {'accountId': accountId});
    final response = await get(url.toString());
    if (response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => Resume.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<List<Resume>> search({
    String? firstName,
    String? lastName,
    String? desiredPosition,
    String? skills,
    int page = 0,
    int size = 20,
  }) async {
    final params = <String, String>{'page': '$page', 'size': '$size'};
    if (firstName != null && firstName.isNotEmpty) params['firstName'] = firstName;
    if (lastName != null && lastName.isNotEmpty) params['lastName'] = lastName;
    if (desiredPosition != null && desiredPosition.isNotEmpty) {
      params['desiredPosition'] = desiredPosition;
    }
    if (skills != null && skills.isNotEmpty) params['skills'] = skills;

    final url = Uri.parse(AppConfig.resumesEndpoint)
        .replace(queryParameters: params);
    final response = await get(url.toString());
    if (response.statusCode != 200) return [];
    final body = jsonDecode(response.body);
    // Support both paginated { content: [...] } and plain list responses.
    if (body is Map<String, dynamic>) {
      final content = body['content'] as List<dynamic>? ?? [];
      return content
          .map((e) => Resume.fromJson(e as Map<String, dynamic>))
          .toList();
    }
    if (body is List<dynamic>) {
      return body
          .map((e) => Resume.fromJson(e as Map<String, dynamic>))
          .toList();
    }
    return [];
  }

  Future<Resume?> findById(String id) async {
    final response = await get('${AppConfig.resumesEndpoint}/$id');
    if (response.statusCode != 200) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Resume?> create(Map<String, dynamic> body) async {
    final response = await post(AppConfig.resumesEndpoint, body);
    if (response.statusCode != 201) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Resume?> update(String id, Map<String, dynamic> body) async {
    final response = await put('${AppConfig.resumesEndpoint}/$id', body);
    if (response.statusCode != 200) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// Throws [ApiException] with the server's message on failure (e.g. an
  /// unsupported file extension/content type), so the caller can show the
  /// real reason instead of silently dropping the photo.
  Future<Resume> uploadPhoto(String id, Uint8List bytes, String filename) async {
    final token = await authService.getAccessToken();
    if (token == null) throw const ApiException('Not authenticated');
    final request = http.MultipartRequest(
      'POST',
      Uri.parse('${AppConfig.resumesEndpoint}/$id/photo'),
    );
    request.headers['Authorization'] = 'Bearer $token';
    request.files.add(http.MultipartFile.fromBytes(
      'file',
      bytes,
      filename: filename,
      // package:http defaults to application/octet-stream when this is
      // omitted, which the backend's content-type allowlist then rejects —
      // must be inferred from the extension explicitly.
      contentType: _photoMediaType(filename),
    ));
    final streamed = await request.send();
    final response = await http.Response.fromStream(streamed);
    if (response.statusCode != 200) throw ApiException(errorDetailOf(response));
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// Kept in sync with the backend allowlist (resume-app's
  /// ResumeAppConstant.ALLOWED_PHOTO_CONTENT_TYPES).
  MediaType _photoMediaType(String filename) {
    final ext = filename.split('.').last.toLowerCase();
    return switch (ext) {
      'png' => MediaType('image', 'png'),
      'webp' => MediaType('image', 'webp'),
      _ => MediaType('image', 'jpeg'),
    };
  }

  Future<bool> deleteResume(String id) async {
    final response = await delete('${AppConfig.resumesEndpoint}/$id');
    return response.statusCode == 204;
  }
}
