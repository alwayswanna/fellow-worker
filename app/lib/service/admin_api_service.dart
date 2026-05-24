import 'dart:convert';

import '../config/app_config.dart';
import '../model/user_profile.dart';
import 'auth_service.dart';
import 'http_client.dart';

class PagedResponse<T> {
  final List<T> content;
  final bool last;
  final int totalElements;

  PagedResponse({required this.content, required this.last, required this.totalElements});

  factory PagedResponse.empty() =>
      PagedResponse(content: [], last: true, totalElements: 0);
}

class AdminApiService extends AuthenticatedHttpClient {
  AdminApiService(super.authService);

  // ── Users ──────────────────────────────────────────────────────────────────

  Future<PagedResponse<UserProfile>> getUsers({int page = 0, int size = 20}) async {
    final url = '${AppConfig.adminUsersEndpoint}?page=$page&size=$size';
    final response = await get(url);
    if (response == null || response.statusCode != 200) return PagedResponse.empty();
    final json = jsonDecode(response.body) as Map<String, dynamic>;
    final content = (json['content'] as List<dynamic>)
        .map((e) => UserProfile.fromJson(e as Map<String, dynamic>))
        .toList();
    return PagedResponse(
      content: content,
      last: json['last'] as bool? ?? true,
      totalElements: json['totalElements'] as int? ?? 0,
    );
  }

  Future<UserProfile?> updateUser(
    String id, {
    String? firstName,
    String? lastName,
    String? birthDate,
    String? password,
  }) async {
    final body = <String, dynamic>{};
    if (firstName != null) body['firstName'] = firstName;
    if (lastName != null) body['lastName'] = lastName;
    if (birthDate != null) body['birthDate'] = birthDate;
    if (password != null && password.isNotEmpty) body['password'] = password;

    final response = await put('${AppConfig.adminUsersEndpoint}/$id', body);
    if (response == null || response.statusCode != 200) return null;
    return UserProfile.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<bool> deleteUser(String id) async {
    final response = await delete('${AppConfig.adminUsersEndpoint}/$id');
    return response != null && response.statusCode == 204;
  }

  // ── Roles ──────────────────────────────────────────────────────────────────

  Future<PagedResponse<UserRole>> getRoles({int page = 0, int size = 20}) async {
    final url = '${AppConfig.adminRolesEndpoint}?page=$page&size=$size';
    final response = await get(url);
    if (response == null || response.statusCode != 200) return PagedResponse.empty();
    final json = jsonDecode(response.body) as Map<String, dynamic>;
    final content = (json['content'] as List<dynamic>)
        .map((e) => UserRole.fromJson(e as Map<String, dynamic>))
        .toList();
    return PagedResponse(
      content: content,
      last: json['last'] as bool? ?? true,
      totalElements: json['totalElements'] as int? ?? 0,
    );
  }

  Future<UserRole?> createRole({
    required String code,
    required String displayName,
    required bool selectable,
  }) async {
    final response = await post(AppConfig.adminRolesEndpoint,
        {'code': code, 'displayName': displayName, 'selectable': selectable});
    if (response == null || response.statusCode != 201) return null;
    return UserRole.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<UserRole?> updateRole(
    String id, {
    String? code,
    String? displayName,
    bool? selectable,
  }) async {
    final body = <String, dynamic>{};
    if (code != null) body['code'] = code;
    if (displayName != null) body['displayName'] = displayName;
    if (selectable != null) body['selectable'] = selectable;

    final response = await put('${AppConfig.adminRolesEndpoint}/$id', body);
    if (response == null || response.statusCode != 200) return null;
    return UserRole.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<bool> deleteRole(String id) async {
    final response = await delete('${AppConfig.adminRolesEndpoint}/$id');
    return response != null && response.statusCode == 204;
  }
}
