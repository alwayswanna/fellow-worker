class AppConfig {
  static const String _gatewayHost =
      String.fromEnvironment('GATEWAY_HOST', defaultValue: 'localhost');
  static const int _gatewayPort =
      int.fromEnvironment('GATEWAY_PORT', defaultValue: 3000);
  static const String _gatewayBase = 'http://$_gatewayHost:$_gatewayPort';

  /// Base URL prefix for user-app resources (e.g. photo URLs returned by the server).
  static const String photoBaseUrl = '$_gatewayBase/user-app';

  static const String clientId = 'flutter-client';
  static const String redirectUri = 'http://localhost:4000/';
  static const List<String> scopes = ['openid', 'profile'];

  // user-app routes
  static const String authorizationEndpoint =
      '$_gatewayBase/user-app/oauth2/authorize';
  static const String tokenEndpoint =
      '$_gatewayBase/user-app/oauth2/token';
  static const String logoutEndpoint =
      '$_gatewayBase/user-app/connect/logout';
  static const String userInfoEndpoint =
      '$_gatewayBase/user-app/api/v1/users/me';
  static const String registerEndpoint =
      '$_gatewayBase/user-app/api/v1/users/register';
  static const String rolesEndpoint =
      '$_gatewayBase/user-app/api/v1/roles';
  static const String adminUsersEndpoint =
      '$_gatewayBase/user-app/api/v1/admin/users';
  static const String adminRolesEndpoint =
      '$_gatewayBase/user-app/api/v1/admin/roles';
  static const String photoUploadEndpoint =
      '$_gatewayBase/user-app/api/v1/users/me/photo';
  static const String changePasswordEndpoint =
      '$_gatewayBase/user-app/api/v1/users/me/password';

  // resume-app routes
  static const String resumesEndpoint =
      '$_gatewayBase/resume-app/api/v1/resumes';

  // vacancy-app routes
  static const String vacanciesEndpoint =
      '$_gatewayBase/vacancy-app/api/v1/vacancies';

  // company-app routes
  static const String companiesEndpoint =
      '$_gatewayBase/company-app/api/v1/companies';
  static const String myCompanyEndpoint =
      '$_gatewayBase/company-app/api/v1/companies/my';

  // user search (non-admin)
  static const String userSearchEndpoint =
      '$_gatewayBase/user-app/api/v1/users/search';
  static const String usersEndpoint =
      '$_gatewayBase/user-app/api/v1/users';

  // applications
  static const String myApplicationsEndpoint =
      '$_gatewayBase/vacancy-app/api/v1/applications/my';
  static String vacancyApplicationsEndpoint(String vacancyId) =>
      '$_gatewayBase/vacancy-app/api/v1/vacancies/$vacancyId/applications';
}
