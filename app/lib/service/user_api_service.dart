import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../config/app_config.dart';
import '../model/user_profile.dart';
import 'http_client.dart';

/// Resolves the real content type for a picked photo. The browser-reported
/// MIME type (`File.type`) is authoritative; the filename extension is only
/// a fallback for when the browser didn't supply one (e.g. a nameless blob).
MediaType _photoContentType(String filename, String? browserMimeType) {
  if (browserMimeType != null && browserMimeType.isNotEmpty) {
    return MediaType.parse(browserMimeType);
  }
  final extension = filename.split('.').last.toLowerCase();
  switch (extension) {
    case 'jpg':
    case 'jpeg':
      return MediaType('image', 'jpeg');
    case 'png':
      return MediaType('image', 'png');
    case 'gif':
      return MediaType('image', 'gif');
    default:
      return MediaType('application', 'octet-stream');
  }
}

class UserApiService extends AuthenticatedHttpClient {
  UserApiService(super.authService);

  /// Session-scoped cache for [getUserById] — applicant/user lookups are
  /// repeated across tabs and across vacancies (e.g. resolving the same
  /// applicant's name on several applications), so avoid refetching a user
  /// we've already resolved this session.
  final Map<String, UserShort> _userByIdCache = {};

  Future<UserProfile?> getMyProfile() async {
    final response = await get(AppConfig.userInfoEndpoint);
    if (response.statusCode != 200) return null;
    return UserProfile.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<List<UserRole>> getRoles() async {
    final response = await sharedHttpClient.get(Uri.parse(AppConfig.rolesEndpoint));
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
    String? photoContentType,
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
        http.MultipartFile.fromBytes(
          'photo',
          photoBytes,
          filename: photoFilename,
          contentType: _photoContentType(photoFilename, photoContentType),
        ),
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
      if (firstName != null) 'firstName': firstName,
      if (lastName != null) 'lastName': lastName,
      if (birthDate != null) 'birthDate': birthDate,
    };
    final response = await put(AppConfig.userInfoEndpoint, body);
    if (response.statusCode != 200) return null;
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
    if (response.statusCode == 204) return null;
    try {
      final decoded = jsonDecode(response.body);
      return decoded['message'] as String? ?? 'Failed to change password';
    } catch (_) {
      return 'Failed to change password';
    }
  }

  Future<UserProfile?> uploadPhoto(
    Uint8List photoBytes,
    String filename, [
    String? contentType,
  ]) async {
    final token = await authService.getAccessToken();
    if (token == null) return null;
    final request = http.MultipartRequest(
      'POST',
      Uri.parse(AppConfig.photoUploadEndpoint),
    );
    request.headers['Authorization'] = 'Bearer $token';
    request.files.add(
      http.MultipartFile.fromBytes(
        'file',
        photoBytes,
        filename: filename,
        contentType: _photoContentType(filename, contentType),
      ),
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
    if (response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => UserShort.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<UserShort?> getUserById(String id) async {
    final cached = _userByIdCache[id];
    if (cached != null) return cached;

    final response = await get('${AppConfig.usersEndpoint}/$id');
    if (response.statusCode != 200) return null;
    final user = UserShort.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
    _userByIdCache[id] = user;
    return user;
  }
}
