import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;

import '../config/app_config.dart';
import '../model/resume.dart';
import 'auth_service.dart';
import 'http_client.dart';

class ResumeApiService extends AuthenticatedHttpClient {
  ResumeApiService(super.authService);

  Future<List<Resume>> findAll() async {
    final response = await get('${AppConfig.resumesEndpoint}/my');
    if (response == null || response.statusCode != 200) return [];
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
    if (response == null || response.statusCode != 200) return [];
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
    if (response == null || response.statusCode != 200) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Resume?> create(Map<String, dynamic> body) async {
    final response = await post(AppConfig.resumesEndpoint, body);
    if (response == null || response.statusCode != 201) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Resume?> update(String id, Map<String, dynamic> body) async {
    final response = await put('${AppConfig.resumesEndpoint}/$id', body);
    if (response == null || response.statusCode != 200) return null;
    return Resume.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Resume?> uploadPhoto(String id, Uint8List bytes, String filename) async {
    final token = await authService.getAccessToken();
    if (token == null) return null;
    final request = http.MultipartRequest(
      'POST',
      Uri.parse('${AppConfig.resumesEndpoint}/$id/photo'),
    );
    request.headers['Authorization'] = 'Bearer $token';
    request.files.add(http.MultipartFile.fromBytes('file', bytes, filename: filename));
    final streamed = await request.send();
    if (streamed.statusCode == 200) {
      final body = await streamed.stream.bytesToString();
      return Resume.fromJson(jsonDecode(body) as Map<String, dynamic>);
    }
    return null;
  }

  Future<bool> deleteResume(String id) async {
    final response = await delete('${AppConfig.resumesEndpoint}/$id');
    return response != null && response.statusCode == 204;
  }
}
