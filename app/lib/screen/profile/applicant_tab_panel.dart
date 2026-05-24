import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../cubit/applications/applications_cubit.dart';
import '../../cubit/saved_vacancies/saved_vacancies_cubit.dart';
import '../../model/resume.dart';
import '../../model/vacancy.dart';
import '../../service/vacancy_api_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/widgets.dart';
import 'applicant_status_tabs.dart';
import 'resumes_panel.dart';

/// APPLICANT role's main tab bar: Vacancies | My Resumes | Companies |
/// My Applications | Saved.
class ApplicantTabPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;
  final List<Resume> resumes;
  final bool isResumesLoading;
  final VoidCallback onAddResume;
  final void Function(Resume) onTapResume;
  final void Function(Resume) onEditResume;
  final void Function(Resume) onDeleteResume;

  const ApplicantTabPanel({
    super.key,
    required this.vacancyApiService,
    this.userRole,
    required this.resumes,
    required this.isResumesLoading,
    required this.onAddResume,
    required this.onTapResume,
    required this.onEditResume,
    required this.onDeleteResume,
  });

  @override
  State<ApplicantTabPanel> createState() => _ApplicantTabPanelState();
}

class _ApplicantTabPanelState extends State<ApplicantTabPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          color: AppColors.surface,
          child: TabBar(
            controller: _tabController,
            isScrollable: true,
            tabAlignment: TabAlignment.start,
            tabs: const [
              Tab(icon: Icon(Icons.work_outline, size: 18), text: 'Vacancies'),
              Tab(icon: Icon(Icons.description_outlined, size: 18), text: 'My Resumes'),
              Tab(icon: Icon(Icons.business_outlined, size: 18), text: 'Companies'),
              Tab(icon: Icon(Icons.send_outlined, size: 18), text: 'My Applications'),
              Tab(icon: Icon(Icons.bookmark_border, size: 18), text: 'Saved'),
            ],
          ),
        ),
        const Divider(height: 1),
        Expanded(
          child: TabBarView(
            controller: _tabController,
            children: [
              JobSearchPanel(
                vacancyApiService: widget.vacancyApiService,
                userRole: widget.userRole,
              ),
              SingleChildScrollView(
                child: ResumesPanel(
                  resumes: widget.resumes,
                  isLoading: widget.isResumesLoading,
                  onAdd: widget.onAddResume,
                  onTap: widget.onTapResume,
                  onEdit: widget.onEditResume,
                  onDelete: widget.onDeleteResume,
                ),
              ),
              _CompanySearchPanel(
                vacancyApiService: widget.vacancyApiService,
                userRole: widget.userRole,
              ),
              BlocProvider(
                create: (_) =>
                    ApplicationsCubit(widget.vacancyApiService)..load(),
                child: MyApplicationsTab(
                  vacancyApiService: widget.vacancyApiService,
                  userRole: widget.userRole,
                ),
              ),
              BlocProvider(
                create: (_) =>
                    SavedVacanciesCubit(widget.vacancyApiService)..load(),
                child: SavedVacanciesTab(
                  vacancyApiService: widget.vacancyApiService,
                  userRole: widget.userRole,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class _CompanySearchPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;

  const _CompanySearchPanel({required this.vacancyApiService, this.userRole});

  @override
  State<_CompanySearchPanel> createState() => _CompanySearchPanelState();
}

class _CompanySearchPanelState extends State<_CompanySearchPanel> {
  final _nameCtrl = TextEditingController();
  final _cityCtrl = TextEditingController();
  bool _isLoading = false;
  List<Company> _companies = [];

  @override
  void initState() {
    super.initState();
    _search();
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _cityCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    setState(() => _isLoading = true);
    final results = await widget.vacancyApiService.searchCompanies(
      name: _nameCtrl.text.trim().isEmpty ? null : _nameCtrl.text.trim(),
      city: _cityCtrl.text.trim().isEmpty ? null : _cityCtrl.text.trim(),
    );
    if (mounted) {
      setState(() {
        _companies = results;
        _isLoading = false;
      });
    }
  }

  void _showDetail(Company c) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => CompanyDetailSheet(
        company: c,
        vacancyApiService: widget.vacancyApiService,
        userRole: widget.userRole,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          color: AppColors.surface,
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
          child: Column(
            children: [
              TextField(
                controller: _nameCtrl,
                decoration: InputDecoration(
                  hintText: 'Company name...',
                  prefixIcon:
                      Icon(Icons.search, color: AppColors.textSecondary),
                  suffixIcon: _nameCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _nameCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: AppColors.border)),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: AppColors.border)),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: AppColors.bg,
                ),
                onSubmitted: (_) => _search(),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _cityCtrl,
                decoration: InputDecoration(
                  hintText: 'City...',
                  prefixIcon: Icon(Icons.location_on_outlined,
                      color: AppColors.textSecondary),
                  suffixIcon: _cityCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _cityCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: AppColors.border)),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: BorderSide(color: AppColors.border)),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: AppColors.bg,
                ),
                onSubmitted: (_) => _search(),
              ),
            ],
          ),
        ),
        Divider(height: 1, color: AppColors.border),
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _companies.isEmpty
                  ? EmptyState(
                      icon: Icons.business_outlined,
                      title: 'No companies found',
                      subtitle: 'Try different keywords or filters',
                      action: OutlinedButton(
                        onPressed: () {
                          _nameCtrl.clear();
                          _cityCtrl.clear();
                          _search();
                        },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: AppColors.primary,
                          side: BorderSide(color: AppColors.primary),
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(8)),
                        ),
                        child: const Text('Reset filters'),
                      ),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.all(16),
                      itemCount: _companies.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (_, i) => _CompanyCard(
                        company: _companies[i],
                        onTap: () => _showDetail(_companies[i]),
                      ),
                    ),
        ),
      ],
    );
  }
}

class _CompanyCard extends StatelessWidget {
  final Company company;
  final VoidCallback onTap;

  const _CompanyCard({required this.company, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Material(
      color: AppColors.surface,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              InitialsAvatar(
                name: company.name,
                radius: 22,
                borderRadius: 8,
                color: AppColors.avatarColor(company.name).withAlpha(38),
                textColor: AppColors.avatarColor(company.name),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(company.name,
                        style: const TextStyle(
                            fontWeight: FontWeight.w600, fontSize: 15)),
                    if (company.industry != null)
                      Text(company.industry!,
                          style: TextStyle(
                              fontSize: 13, color: AppColors.textSecondary)),
                    const SizedBox(height: 4),
                    Wrap(
                      spacing: 12,
                      children: [
                        if (company.city != null)
                          MetaTag(Icons.location_on_outlined, company.city!),
                        if (company.size != null)
                          MetaTag(
                              Icons.people_outline, company.size!.displayName),
                        MetaTag(Icons.star_outline,
                            company.rating.toStringAsFixed(1)),
                      ],
                    ),
                  ],
                ),
              ),
              Icon(Icons.chevron_right,
                  size: 20, color: AppColors.border),
            ],
          ),
        ),
      ),
    );
  }
}
