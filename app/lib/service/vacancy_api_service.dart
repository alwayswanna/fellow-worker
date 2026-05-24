import 'dart:convert';

import '../config/app_config.dart';
import '../model/application.dart';
import '../model/vacancy.dart';
import 'auth_service.dart';
import 'http_client.dart';

class VacancyApiService extends AuthenticatedHttpClient {
  VacancyApiService(super.authService);

  // ── Vacancies ─────────────────────────────────────────────────────────────

  Future<List<Vacancy>> searchVacancies({
    String? title,
    String? companyId,
    String? city,
    EmploymentType? employmentType,
    WorkFormat? workFormat,
    ExperienceLevel? experienceLevel,
    VacancyStatus status = VacancyStatus.ACTIVE,
    int page = 0,
    int size = 20,
  }) async {
    final params = <String, String>{
      'status': status.name,
      'page': '$page',
      'size': '$size',
    };
    if (title != null && title.isNotEmpty) params['title'] = title;
    if (companyId != null) params['companyId'] = companyId;
    if (city != null && city.isNotEmpty) params['city'] = city;
    if (employmentType != null) params['employmentType'] = employmentType.name;
    if (workFormat != null) params['workFormat'] = workFormat.name;
    if (experienceLevel != null) params['experienceLevel'] = experienceLevel.name;

    final url = Uri.parse(AppConfig.vacanciesEndpoint).replace(queryParameters: params);
    final response = await get(url.toString());
    if (response == null || response.statusCode != 200) return [];

    final body = jsonDecode(response.body) as Map<String, dynamic>;
    final content = body['content'] as List<dynamic>;
    return content.map((e) => Vacancy.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<Vacancy?> createVacancy(Map<String, dynamic> body) async {
    final response = await post(AppConfig.vacanciesEndpoint, body);
    if (response == null || response.statusCode != 201) return null;
    return Vacancy.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Vacancy?> updateVacancy(String id, Map<String, dynamic> body) async {
    final response = await put('${AppConfig.vacanciesEndpoint}/$id', body);
    if (response == null || response.statusCode != 200) return null;
    return Vacancy.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<bool> deleteVacancy(String id) async {
    final response = await delete('${AppConfig.vacanciesEndpoint}/$id');
    return response != null && response.statusCode == 204;
  }

  // ── Companies ─────────────────────────────────────────────────────────────

  Future<List<Company>> searchCompanies({
    String? name,
    String? industry,
    String? city,
    int page = 0,
  }) async {
    final params = <String, String>{'page': '$page'};
    if (name != null && name.isNotEmpty) params['name'] = name;
    if (industry != null && industry.isNotEmpty) params['industry'] = industry;
    if (city != null && city.isNotEmpty) params['city'] = city;

    final url = Uri.parse(AppConfig.companiesEndpoint).replace(queryParameters: params);
    final response = await get(url.toString());
    if (response == null || response.statusCode != 200) return [];

    final body = jsonDecode(response.body) as Map<String, dynamic>;
    final content = body['content'] as List<dynamic>;
    return content.map((e) => Company.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<Company?> createCompany(Map<String, dynamic> body) async {
    final response = await post(AppConfig.companiesEndpoint, body);
    if (response == null || response.statusCode != 201) return null;
    return Company.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Company?> updateCompany(String id, Map<String, dynamic> body) async {
    final response = await put('${AppConfig.companiesEndpoint}/$id', body);
    if (response == null || response.statusCode != 200) return null;
    return Company.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<bool> deleteCompany(String id) async {
    final response = await delete('${AppConfig.companiesEndpoint}/$id');
    return response != null && response.statusCode == 204;
  }

  Future<Company?> getMyCompany() async {
    final response = await get(AppConfig.myCompanyEndpoint);
    if (response == null || response.statusCode == 204) return null;
    if (response.statusCode != 200) return null;
    return Company.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<Company?> getCompanyById(String id) async {
    final response = await get('${AppConfig.companiesEndpoint}/$id');
    if (response == null || response.statusCode != 200) return null;
    return Company.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  // ── Recruiters ────────────────────────────────────────────────────────────

  Future<List<CompanyRecruiter>> getRecruiters(String companyId) async {
    final url = Uri.parse('${AppConfig.companiesEndpoint}/$companyId/recruiters')
        .replace(queryParameters: {'size': '50'});
    final response = await get(url.toString());
    if (response == null || response.statusCode != 200) return [];
    final body = jsonDecode(response.body) as Map<String, dynamic>;
    final content = body['content'] as List<dynamic>;
    return content
        .map((e) => CompanyRecruiter.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<bool> addRecruiter(String companyId, String accountId) async {
    final response = await post(
      '${AppConfig.companiesEndpoint}/$companyId/recruiters',
      {'accountId': accountId},
    );
    return response != null && response.statusCode == 201;
  }

  Future<bool> removeRecruiter(String companyId, String accountId) async {
    final response = await delete(
        '${AppConfig.companiesEndpoint}/$companyId/recruiters/$accountId');
    return response != null && response.statusCode == 204;
  }

  // ── Applications ──────────────────────────────────────────────────────────

  Future<Application?> applyToVacancy(String vacancyId) async {
    final response = await post(AppConfig.vacancyApplicationsEndpoint(vacancyId), {});
    if (response == null || response.statusCode != 201) return null;
    return Application.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<bool> withdrawApplication(String vacancyId) async {
    final response = await delete(AppConfig.vacancyApplicationsEndpoint(vacancyId));
    return response != null && response.statusCode == 204;
  }

  Future<Application?> getMyApplicationForVacancy(String vacancyId) async {
    final response =
        await get('${AppConfig.vacancyApplicationsEndpoint(vacancyId)}/my');
    if (response == null || response.statusCode != 200) return null;
    final body = jsonDecode(response.body);
    if (body == null) return null;
    return Application.fromJson(body as Map<String, dynamic>);
  }

  Future<List<Application>> getMyApplications() async {
    final response = await get(AppConfig.myApplicationsEndpoint);
    if (response == null || response.statusCode != 200) return [];
    final list = jsonDecode(response.body) as List<dynamic>;
    return list.map((e) => Application.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<List<Application>> getVacancyApplications(String vacancyId) async {
    final response = await get(AppConfig.vacancyApplicationsEndpoint(vacancyId));
    if (response == null || response.statusCode != 200) return [];
    final body = jsonDecode(response.body) as Map<String, dynamic>;
    final content = body['content'] as List<dynamic>;
    return content
        .map((e) => Application.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<Application?> updateApplicationStatus(
      String vacancyId, String applicationId, ApplicationStatus status) async {
    final response = await put(
      '${AppConfig.vacancyApplicationsEndpoint(vacancyId)}/$applicationId/status',
      {'status': status.name},
    );
    if (response == null || response.statusCode != 200) return null;
    return Application.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }
}
