import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;

import '../config/app_config.dart';
import '../model/user_profile.dart';
import 'auth_service.dart';
import 'http_client.dart';

class UserApiService extends AuthenticatedHttpClient {
  UserApiService(super.authService);

  Future<UserProfile?> getMyProfile() async {
    final response = await get(AppConfig.userInfoEndpoint);
    if (response == null || response.statusCode != 200) return null;
    return UserProfile.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<List<UserRole>> getRoles() async {
    final response = await http.get(Uri.parse(AppConfig.rolesEndpoint));
    if (response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => UserRole.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<bool> register({
    required String login,
    required String password,
    required String firstName,
    required String lastName,
    required String birthDate,
    required String roleCode,
    Uint8List? photoBytes,
    String? photoFilename,
  }) async {
    final request = http.MultipartRequest(
      'POST',
      Uri.parse(AppConfig.registerEndpoint),
    );
    request.fields['login'] = login;
    request.fields['password'] = password;
    request.fields['firstName'] = firstName;
    request.fields['lastName'] = lastName;
    request.fields['birthDate'] = birthDate;
    request.fields['code'] = roleCode;

    if (photoBytes != null && photoFilename != null) {
      request.files.add(
        http.MultipartFile.fromBytes('photo', photoBytes, filename: photoFilename),
      );
    }

    final streamed = await request.send();
    return streamed.statusCode == 200 || streamed.statusCode == 201;
  }

  Future<UserProfile?> updateProfile({
    String? firstName,
    String? lastName,
    String? birthDate,
  }) async {
    final body = <String, dynamic>{
      ?'firstName': firstName,
      ?'lastName': lastName,
      ?'birthDate': birthDate,
    };
    final response = await put(AppConfig.userInfoEndpoint, body);
    if (response == null || response.statusCode != 200) return null;
    return UserProfile.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// Returns null on success, or an error message string on failure.
  Future<String?> changePassword({
    required String oldPassword,
    required String newPassword,
    required String confirmPassword,
  }) async {
    final response = await put(AppConfig.changePasswordEndpoint, {
      'oldPassword': oldPassword,
      'newPassword': newPassword,
      'confirmPassword': confirmPassword,
    });
    if (response == null) return 'Not authenticated';
    if (response.statusCode == 204) return null;
    try {
      final decoded = jsonDecode(response.body);
      return decoded['message'] as String? ?? 'Failed to change password';
    } catch (_) {
      return 'Failed to change password';
    }
  }

  Future<UserProfile?> uploadPhoto(Uint8List photoBytes, String filename) async {
    final token = await authService.getAccessToken();
    if (token == null) return null;
    final request = http.MultipartRequest(
      'POST',
      Uri.parse(AppConfig.photoUploadEndpoint),
    );
    request.headers['Authorization'] = 'Bearer $token';
    request.files.add(
      http.MultipartFile.fromBytes('file', photoBytes, filename: filename),
    );
    final streamed = await request.send();
    if (streamed.statusCode == 200 || streamed.statusCode == 201) {
      final body = await streamed.stream.bytesToString();
      return UserProfile.fromJson(jsonDecode(body) as Map<String, dynamic>);
    }
    return null;
  }

  Future<List<UserShort>> searchUsers(String query) async {
    final url = Uri.parse(AppConfig.userSearchEndpoint)
        .replace(queryParameters: {'query': query, 'size': '20'});
    final response = await get(url.toString());
    if (response == null || response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => UserShort.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<UserShort?> getUserById(String id) async {
    final response = await get('${AppConfig.usersEndpoint}/$id');
    if (response == null || response.statusCode != 200) return null;
    return UserShort.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }
}
