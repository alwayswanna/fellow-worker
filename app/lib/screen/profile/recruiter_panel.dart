import 'package:flutter/material.dart';

import '../../model/application.dart';
import '../../model/user_profile.dart';
import '../../model/vacancy.dart';
import '../../service/resume_api_service.dart';
import '../../service/user_api_service.dart';
import '../../service/vacancy_api_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/widgets.dart';
import '../resume/applicant_resume_screen.dart';
import 'shared_tabs.dart';

/// RECRUITER role's main tab bar: Vacancies | Applications | Applicants.
class RecruiterPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;
  final ResumeApiService resumeApiService;

  const RecruiterPanel({
    super.key,
    required this.vacancyApiService,
    required this.userApiService,
    required this.resumeApiService,
  });

  @override
  State<RecruiterPanel> createState() => _RecruiterPanelState();
}

class _RecruiterPanelState extends State<RecruiterPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;
  Company? _company;
  bool _isLoadingCompany = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _loadCompany();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadCompany() async {
    final company = await widget.vacancyApiService.getMyCompany();
    if (mounted) setState(() { _company = company; _isLoadingCompany = false; });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingCompany) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_company == null) {
      return Center(
        child: Padding(
          padding: EdgeInsets.all(32),
          child: Text('You are not assigned to any company yet.',
              textAlign: TextAlign.center,
              style: TextStyle(color: AppColors.textSecondary)),
        ),
      );
    }
    return Column(children: [
      Container(
        color: AppColors.surface,
        child: TabBar(
          controller: _tabController,
          labelColor: AppColors.primary,
          unselectedLabelColor: AppColors.textSecondary,
          indicatorColor: AppColors.primary,
          tabs: const [
            Tab(icon: Icon(Icons.work_outline, size: 18), text: 'Vacancies'),
            Tab(icon: Icon(Icons.inbox_outlined, size: 18), text: 'Applications'),
            Tab(icon: Icon(Icons.person_search_outlined, size: 18), text: 'Applicants'),
          ],
        ),
      ),
      Divider(height: 1, color: AppColors.border),
      Expanded(
        child: TabBarView(
          controller: _tabController,
          children: [
            VacanciesTab(
              company: _company,
              vacancyApiService: widget.vacancyApiService,
            ),
            RecruiterApplicationsTab(
              company: _company!,
              vacancyApiService: widget.vacancyApiService,
              userApiService: widget.userApiService,
              resumeApiService: widget.resumeApiService,
            ),
            _ApplicantSearchTab(
              userApiService: widget.userApiService,
              vacancyApiService: widget.vacancyApiService,
              resumeApiService: widget.resumeApiService,
              company: _company!,
            ),
          ],
        ),
      ),
    ]);
  }
}

// ── Applicant search tab (Recruiter) ──────────────────────────────────────────

class _ApplicantSearchTab extends StatefulWidget {
  final UserApiService userApiService;
  final VacancyApiService vacancyApiService;
  final ResumeApiService resumeApiService;
  final Company company;

  const _ApplicantSearchTab({
    required this.userApiService,
    required this.vacancyApiService,
    required this.resumeApiService,
    required this.company,
  });

  @override
  State<_ApplicantSearchTab> createState() => _ApplicantSearchTabState();
}

class _ApplicantSearchTabState extends State<_ApplicantSearchTab> {
  final _searchCtrl = TextEditingController();
  List<UserShort> _results = [];
  bool _isLoading = false;

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    final q = _searchCtrl.text.trim();
    if (q.isEmpty) return;
    setState(() => _isLoading = true);
    final results = await widget.userApiService.searchUsers(q);
    if (mounted) setState(() { _results = results; _isLoading = false; });
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Container(
        color: AppColors.surface,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: TextField(
          controller: _searchCtrl,
          decoration: InputDecoration(
            hintText: 'Search by name or login...',
            prefixIcon: Icon(Icons.search, color: AppColors.textSecondary),
            suffixIcon: _searchCtrl.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear, size: 18),
                    onPressed: () {
                      _searchCtrl.clear();
                      setState(() => _results = []);
                    })
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
          onChanged: (_) => setState(() {}),
        ),
      ),
      Divider(height: 1, color: AppColors.border),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _results.isEmpty
                ? Center(child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.person_search_outlined,
                          size: 48, color: AppColors.border),
                      const SizedBox(height: 12),
                      Text('Search for applicants by name or login',
                          style: TextStyle(
                              fontSize: 14, color: AppColors.textSecondary)),
                    ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _results.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (_, i) {
                      final u = _results[i];
                      return Material(
                        color: AppColors.surface,
                        borderRadius: BorderRadius.circular(10),
                        child: ListTile(
                          onTap: () => showModalBottomSheet(
                            context: context,
                            isScrollControlled: true,
                            backgroundColor: Colors.transparent,
                            builder: (_) => _ApplicantApplicationsSheet(
                              user: u,
                              company: widget.company,
                              vacancyApiService: widget.vacancyApiService,
                              resumeApiService: widget.resumeApiService,
                            ),
                          ),
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: 16, vertical: 4),
                          leading: CircleAvatar(
                            backgroundColor: AppColors.tagBg,
                            child: Text(
                              u.firstName[0].toUpperCase(),
                              style: TextStyle(
                                  color: AppColors.primary,
                                  fontWeight: FontWeight.bold),
                            ),
                          ),
                          title: Text(u.fullName,
                              style: const TextStyle(
                                  fontWeight: FontWeight.w600, fontSize: 14)),
                          subtitle: Text('@${u.login}',
                              style: TextStyle(
                                  fontSize: 12, color: AppColors.textSecondary)),
                          trailing: Icon(Icons.chevron_right,
                              color: AppColors.textSecondary),
                        ),
                      );
                    },
                  ),
      ),
    ]);
  }
}

// ── Applicant applications sheet ──────────────────────────────────────────────

/// Shows which of the company's vacancies [user] has applied to, and lets
/// the recruiter view their resume.
///
/// There's no backend endpoint to look applications up by applicant id (only
/// by vacancy, or "my own") — so the applications list aggregates
/// client-side: fetch every vacancy for the company, fetch its applications,
/// and filter to this user. Fine at small/medium vacancy counts; would need
/// a real backend endpoint to scale. The resume lookup, unlike applications,
/// does have a real backend endpoint (`GET /resumes?accountId=`,
/// recruiter/company-founder only).
class _ApplicantApplicationsSheet extends StatefulWidget {
  final UserShort user;
  final Company company;
  final VacancyApiService vacancyApiService;
  final ResumeApiService resumeApiService;

  const _ApplicantApplicationsSheet({
    required this.user,
    required this.company,
    required this.vacancyApiService,
    required this.resumeApiService,
  });

  @override
  State<_ApplicantApplicationsSheet> createState() =>
      _ApplicantApplicationsSheetState();
}

class _ApplicantApplicationsSheetState
    extends State<_ApplicantApplicationsSheet> {
  List<({Vacancy vacancy, Application application})> _entries = [];
  bool _isLoading = true;
  bool _isLoadingResume = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _viewResume() async {
    setState(() => _isLoadingResume = true);
    final resumes = await widget.resumeApiService.findAllByAccountId(widget.user.id);
    if (!mounted) return;
    setState(() => _isLoadingResume = false);
    Navigator.of(context).push(MaterialPageRoute(
      builder: (_) => ApplicantResumeScreen(
        applicantName: widget.user.fullName,
        resumes: resumes,
      ),
    ));
  }

  Future<void> _load() async {
    final byStatus = await Future.wait([
      widget.vacancyApiService.searchVacancies(
          companyId: widget.company.id, status: VacancyStatus.ACTIVE),
      widget.vacancyApiService.searchVacancies(
          companyId: widget.company.id, status: VacancyStatus.DRAFT),
      widget.vacancyApiService.searchVacancies(
          companyId: widget.company.id, status: VacancyStatus.CLOSED),
    ]);
    final vacancies = [...byStatus[0], ...byStatus[1], ...byStatus[2]];
    final applicationsPerVacancy = await Future.wait(
      vacancies.map((v) => widget.vacancyApiService.getVacancyApplications(v.id)),
    );

    final entries = <({Vacancy vacancy, Application application})>[];
    for (var i = 0; i < vacancies.length; i++) {
      for (final app in applicationsPerVacancy[i]) {
        if (app.applicantAccountId == widget.user.id) {
          entries.add((vacancy: vacancies[i], application: app));
        }
      }
    }
    if (mounted) setState(() { _entries = entries; _isLoading = false; });
  }

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.6,
      minChildSize: 0.3,
      maxChildSize: 0.9,
      builder: (_, ctrl) => Container(
        decoration: BoxDecoration(
          color: AppColors.surface,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(children: [
          const BottomSheetHandle(),
          Padding(
            padding: const EdgeInsets.fromLTRB(24, 8, 24, 12),
            child: Row(children: [
              CircleAvatar(
                radius: 22,
                backgroundColor: AppColors.tagBg,
                child: Text(
                  widget.user.firstName.isNotEmpty
                      ? widget.user.firstName[0].toUpperCase()
                      : '?',
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                      color: AppColors.primary),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(widget.user.fullName,
                      style: const TextStyle(
                          fontSize: 16, fontWeight: FontWeight.bold)),
                  Text('@${widget.user.login}',
                      style: TextStyle(fontSize: 13, color: AppColors.textSecondary)),
                ]),
              ),
              OutlinedButton.icon(
                onPressed: _isLoadingResume ? null : _viewResume,
                icon: _isLoadingResume
                    ? SizedBox(
                        width: 14,
                        height: 14,
                        child: CircularProgressIndicator(
                            strokeWidth: 2, color: AppColors.primary),
                      )
                    : const Icon(Icons.description_outlined, size: 16),
                label: const Text('Resume'),
                style: OutlinedButton.styleFrom(
                  foregroundColor: AppColors.primary,
                  side: BorderSide(color: AppColors.primary),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ]),
          ),
          const Divider(height: 1),
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _entries.isEmpty
                    ? const EmptyState(
                        icon: Icons.inbox_outlined,
                        title: 'No applications to your vacancies',
                        subtitle:
                            "This applicant hasn't applied to any of your company's vacancies.",
                      )
                    : ListView.separated(
                        controller: ctrl,
                        padding: const EdgeInsets.all(16),
                        itemCount: _entries.length,
                        separatorBuilder: (_, __) => const SizedBox(height: 8),
                        itemBuilder: (_, i) {
                          final e = _entries[i];
                          return Material(
                            color: AppColors.bg,
                            borderRadius: BorderRadius.circular(10),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 16, vertical: 12),
                              child: Row(children: [
                                Expanded(
                                  child: Text(e.vacancy.title,
                                      style: const TextStyle(
                                          fontWeight: FontWeight.w600, fontSize: 14)),
                                ),
                                StatusBadge(e.application.status),
                              ]),
                            ),
                          );
                        },
                      ),
          ),
        ]),
      ),
    );
  }
}
