import 'package:flutter/material.dart';

import '../../model/vacancy.dart';
import '../../service/user_api_service.dart';
import '../../service/vacancy_api_service.dart';
import '../../theme/app_theme.dart';
import 'company_owner/company_tab.dart';
import 'company_owner/recruiters_tab.dart';
import 'shared_tabs.dart';

/// COMPANY_FOUNDER role's main tab bar: Company | Vacancies | Recruiters |
/// Applications.
class CompanyOwnerPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const CompanyOwnerPanel({
    super.key,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<CompanyOwnerPanel> createState() => _CompanyOwnerPanelState();
}

class _CompanyOwnerPanelState extends State<CompanyOwnerPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;
  Company? _company;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _loadMyCompany();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadMyCompany() async {
    setState(() => _isLoading = true);
    final company = await widget.vacancyApiService.getMyCompany();
    if (mounted) {
      setState(() {
        _company = company;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    return Column(
      children: [
        Container(
          color: AppColors.surface,
          child: TabBar(
            controller: _tabController,
            labelColor: AppColors.primary,
            unselectedLabelColor: AppColors.textSecondary,
            indicatorColor: AppColors.primary,
            tabs: const [
              Tab(icon: Icon(Icons.business_outlined, size: 18), text: 'Company'),
              Tab(icon: Icon(Icons.work_outline, size: 18), text: 'Vacancies'),
              Tab(icon: Icon(Icons.people_outline, size: 18), text: 'Recruiters'),
              Tab(icon: Icon(Icons.inbox_outlined, size: 18), text: 'Applications'),
            ],
          ),
        ),
        Divider(height: 1, color: AppColors.border),
        Expanded(
          child: TabBarView(
            controller: _tabController,
            children: [
              CompanyTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                onCompanyChanged: (c) => setState(() => _company = c),
              ),
              VacanciesTab(company: _company, vacancyApiService: widget.vacancyApiService),
              RecruitersTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                userApiService: widget.userApiService,
              ),
              RecruiterApplicationsTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                userApiService: widget.userApiService,
              ),
            ],
          ),
        ),
      ],
    );
  }
}
