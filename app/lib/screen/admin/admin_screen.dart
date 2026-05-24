import 'package:flutter/material.dart';

import '../../service/admin_api_service.dart';
import '../../service/resume_api_service.dart';
import '../../service/vacancy_api_service.dart';
import 'admin_companies_tab.dart';
import 'admin_resumes_tab.dart';
import 'admin_roles_tab.dart';
import 'admin_users_tab.dart';
import 'admin_vacancies_tab.dart';

class AdminScreen extends StatelessWidget {
  final AdminApiService adminApiService;
  final VacancyApiService vacancyApiService;
  final ResumeApiService resumeApiService;

  const AdminScreen({
    super.key,
    required this.adminApiService,
    required this.vacancyApiService,
    required this.resumeApiService,
  });

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 5,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Admin panel'),
          backgroundColor: Colors.indigo,
          foregroundColor: Colors.white,
          bottom: const TabBar(
            labelColor: Colors.white,
            unselectedLabelColor: Colors.white70,
            indicatorColor: Colors.white,
            isScrollable: true,
            tabs: [
              Tab(icon: Icon(Icons.people_outline), text: 'Users'),
              Tab(icon: Icon(Icons.shield_outlined), text: 'Roles'),
              Tab(icon: Icon(Icons.business_outlined), text: 'Companies'),
              Tab(icon: Icon(Icons.work_outline), text: 'Vacancies'),
              Tab(icon: Icon(Icons.description_outlined), text: 'Resumes'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            AdminUsersTab(adminApiService: adminApiService),
            AdminRolesTab(adminApiService: adminApiService),
            AdminCompaniesTab(vacancyApiService: vacancyApiService),
            AdminVacanciesTab(vacancyApiService: vacancyApiService),
            AdminResumesTab(resumeApiService: resumeApiService),
          ],
        ),
      ),
    );
  }
}
