import 'package:flutter/material.dart';

import '../../model/user_profile.dart';
import '../../model/vacancy.dart';
import '../../service/user_api_service.dart';
import '../../service/vacancy_api_service.dart';
import '../../theme/app_theme.dart';
import 'shared_tabs.dart';

/// RECRUITER role's main tab bar: Vacancies | Applications | Applicants.
class RecruiterPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const RecruiterPanel({
    super.key,
    required this.vacancyApiService,
    required this.userApiService,
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
            ),
            _ApplicantSearchTab(
              userApiService: widget.userApiService,
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

  const _ApplicantSearchTab({required this.userApiService});

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
                        ),
                      );
                    },
                  ),
      ),
    ]);
  }
}
