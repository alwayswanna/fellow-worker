class UserRole {
  final String id;
  final String code;
  final String displayName;
  final bool selectable;

  UserRole({
    required this.id,
    required this.code,
    required this.displayName,
    this.selectable = true,
  });

  factory UserRole.fromJson(Map<String, dynamic> json) {
    return UserRole(
      id: json['id'] as String,
      code: json['code'] as String,
      displayName: json['displayName'] as String,
      selectable: json['selectable'] as bool? ?? true,
    );
  }
}

class UserShort {
  final String id;
  final String login;
  final String firstName;
  final String lastName;

  UserShort({
    required this.id,
    required this.login,
    required this.firstName,
    required this.lastName,
  });

  String get fullName => '$firstName $lastName';

  factory UserShort.fromJson(Map<String, dynamic> json) => UserShort(
        id: json['id'] as String,
        login: json['login'] as String,
        firstName: json['firstName'] as String,
        lastName: json['lastName'] as String,
      );
}

class UserProfile {
  final String id;
  final String login;
  final String firstName;
  final String lastName;
  final String birthDate;
  final UserRole? role;
  final String? photoUrl;
  final String? createdAt;
  final String? updatedAt;
  final String? createdBy;
  final String? updatedBy;

  UserProfile({
    required this.id,
    required this.login,
    required this.firstName,
    required this.lastName,
    required this.birthDate,
    this.role,
    this.photoUrl,
    this.createdAt,
    this.updatedAt,
    this.createdBy,
    this.updatedBy,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      id: json['id'] as String,
      login: json['login'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      birthDate: json['birthDate'] as String,
      role: json['role'] != null
          ? UserRole.fromJson(json['role'] as Map<String, dynamic>)
          : null,
      photoUrl: json['photoUrl'] as String?,
      createdAt: json['createdAt'] as String?,
      updatedAt: json['updatedAt'] as String?,
      createdBy: json['createdBy'] as String?,
      updatedBy: json['updatedBy'] as String?,
    );
  }

  String get fullName => '$firstName $lastName';
}
