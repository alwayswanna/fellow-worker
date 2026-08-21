import 'package:flutter/material.dart';

import '../model/application.dart';
import '../model/vacancy.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import '../util/formatters.dart';
import 'bottom_sheet_handle.dart';
import 'empty_state.dart';

/// Read-only vacancy browser: search bar, filters, and results list.
/// Works both for authenticated users (with role-aware actions) and guests
/// (pass [onSignInToApply] to prompt sign-in instead of applying directly).
class JobSearchPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;
  final VoidCallback? onSignInToApply;

  const JobSearchPanel({
    super.key,
    required this.vacancyApiService,
    this.userRole,
    this.onSignInToApply,
  });

  @override
  State<JobSearchPanel> createState() => _JobSearchPanelState();
}

class _JobSearchPanelState extends State<JobSearchPanel> {
  final _searchCtrl = TextEditingController();
  bool _isLoading = false;
  bool _firstLoad = false;
  List<Vacancy> _vacancies = [];

  EmploymentType? _typeFilter;
  WorkFormat? _formatFilter;
  ExperienceLevel? _levelFilter;

  @override
  void initState() {
    super.initState();
    _search();
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    setState(() {
      _isLoading = true;
      if (!_firstLoad) _firstLoad = true;
    });
    final results = await widget.vacancyApiService.searchVacancies(
      title: _searchCtrl.text.trim().isEmpty ? null : _searchCtrl.text.trim(),
      employmentType: _typeFilter,
      workFormat: _formatFilter,
      experienceLevel: _levelFilter,
    );
    if (mounted) {
      setState(() {
        _vacancies = results;
        _isLoading = false;
      });
    }
  }

  void _showVacancyDetail(Vacancy v) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => VacancyDetailSheet(
        vacancy: v,
        vacancyApiService: widget.vacancyApiService,
        userRole: widget.userRole,
        onSignInToApply: widget.onSignInToApply,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Search bar
        Container(
          color: AppColors.surface,
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
          child: Column(
            children: [
              TextField(
                controller: _searchCtrl,
                decoration: InputDecoration(
                  hintText: 'Job title, skill, company...',
                  prefixIcon: const Icon(Icons.search, color: AppColors.textSecondary),
                  suffixIcon: _searchCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _searchCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: AppColors.border)),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: AppColors.border)),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: AppColors.bg,
                ),
                onSubmitted: (_) => _search(),
              ),
              const SizedBox(height: 10),
              // Filter chips
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    FilterPopup<EmploymentType?>(
                      label: _typeFilter?.displayName ?? 'Employment',
                      isActive: _typeFilter != null,
                      options: [
                        (null, 'Any'),
                        ...EmploymentType.values.map((e) => (e, e.displayName)),
                      ],
                      value: _typeFilter,
                      onChanged: (v) {
                        setState(() => _typeFilter = v);
                        _search();
                      },
                    ),
                    const SizedBox(width: 8),
                    FilterPopup<WorkFormat?>(
                      label: _formatFilter?.displayName ?? 'Format',
                      isActive: _formatFilter != null,
                      options: [
                        (null, 'Any'),
                        ...WorkFormat.values.map((e) => (e, e.displayName)),
                      ],
                      value: _formatFilter,
                      onChanged: (v) {
                        setState(() => _formatFilter = v);
                        _search();
                      },
                    ),
                    const SizedBox(width: 8),
                    FilterPopup<ExperienceLevel?>(
                      label: _levelFilter?.displayName ?? 'Experience',
                      isActive: _levelFilter != null,
                      options: [
                        (null, 'Any'),
                        ...ExperienceLevel.values.map((e) => (e, e.displayName)),
                      ],
                      value: _levelFilter,
                      onChanged: (v) {
                        setState(() => _levelFilter = v);
                        _search();
                      },
                    ),
                    if (_typeFilter != null || _formatFilter != null || _levelFilter != null) ...[
                      const SizedBox(width: 8),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            _typeFilter = null;
                            _formatFilter = null;
                            _levelFilter = null;
                          });
                          _search();
                        },
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 10, vertical: 7),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(999),
                            border: Border.all(color: AppColors.border),
                          ),
                          child: const Text('Clear all',
                              style: TextStyle(
                                  fontSize: 13, color: AppColors.textSecondary)),
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              const SizedBox(height: 8),
            ],
          ),
        ),
        const Divider(height: 1),
        // Results
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _vacancies.isEmpty
                  ? EmptyState(
                      icon: Icons.search_off_outlined,
                      title: 'No vacancies found',
                      subtitle: 'Try different keywords or filters',
                      action: OutlinedButton(
                        onPressed: () {
                          _searchCtrl.clear();
                          setState(() {
                            _typeFilter = null;
                            _formatFilter = null;
                            _levelFilter = null;
                          });
                          _search();
                        },
                        child: const Text('Reset filters'),
                      ),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.all(16),
                      itemCount: _vacancies.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (_, i) => VacancyBrowseCard(
                        vacancy: _vacancies[i],
                        onTap: () => _showVacancyDetail(_vacancies[i]),
                        vacancyApiService: widget.vacancyApiService,
                        canSave: widget.userRole != null,
                        onSignInToApply: widget.onSignInToApply,
                      ),
                    ),
        ),
      ],
    );
  }
}

class FilterPopup<T> extends StatelessWidget {
  final String label;
  final bool isActive;
  final List<(T, String)> options;
  final T value;
  final void Function(T) onChanged;

  const FilterPopup({
    super.key,
    required this.label,
    required this.isActive,
    required this.options,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return PopupMenuButton<T>(
      initialValue: value,
      onSelected: onChanged,
      itemBuilder: (_) => options
          .map((opt) => PopupMenuItem<T>(value: opt.$1, child: Text(opt.$2)))
          .toList(),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
        decoration: BoxDecoration(
          color: isActive ? AppColors.tagBg : Colors.transparent,
          borderRadius: BorderRadius.circular(999),
          border: Border.all(
            color: isActive ? AppColors.primary : AppColors.border,
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(label,
                style: TextStyle(
                    fontSize: 13,
                    color:
                        isActive ? AppColors.primary : AppColors.textSecondary)),
            const SizedBox(width: 4),
            Icon(Icons.arrow_drop_down,
                size: 18,
                color: isActive ? AppColors.primary : AppColors.textSecondary),
          ],
        ),
      ),
    );
  }
}

class VacancyBrowseCard extends StatefulWidget {
  final Vacancy vacancy;
  final VoidCallback onTap;
  final VacancyApiService? vacancyApiService;
  final bool canSave;
  final bool initiallySaved;
  final VoidCallback? onSignInToApply;

  const VacancyBrowseCard({
    super.key,
    required this.vacancy,
    required this.onTap,
    this.vacancyApiService,
    this.canSave = false,
    this.initiallySaved = false,
    this.onSignInToApply,
  });

  @override
  State<VacancyBrowseCard> createState() => _VacancyBrowseCardState();
}

class _VacancyBrowseCardState extends State<VacancyBrowseCard> {
  late bool _isSaved = widget.initiallySaved;
  bool _isSaving = false;

  Future<void> _toggleSave() async {
    if (!widget.canSave || widget.vacancyApiService == null) {
      widget.onSignInToApply?.call();
      return;
    }
    final wasSaved = _isSaved;
    setState(() {
      _isSaved = !wasSaved;
      _isSaving = true;
    });
    final ok = _isSaved
        ? await widget.vacancyApiService!.saveVacancy(widget.vacancy.id)
        : await widget.vacancyApiService!.unsaveVacancy(widget.vacancy.id);
    if (mounted) {
      setState(() {
        _isSaving = false;
        if (!ok) _isSaved = wasSaved;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final vacancy = widget.vacancy;
    return Material(
      color: AppColors.surface,
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: widget.onTap,
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: AppColors.border),
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      color: AppColors.tagBg,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Text(
                      (vacancy.company?.name ?? '').isNotEmpty
                          ? vacancy.company!.name[0].toUpperCase()
                          : '?',
                      style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: AppColors.tagText),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(vacancy.title,
                            style: const TextStyle(
                                fontWeight: FontWeight.w700, fontSize: 15)),
                        Text(vacancy.company?.name ?? '',
                            style: const TextStyle(
                                fontSize: 13, color: AppColors.textSecondary)),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: _isSaving ? null : _toggleSave,
                    tooltip: _isSaved ? 'Remove from saved' : 'Save vacancy',
                    icon: Icon(
                      _isSaved ? Icons.bookmark : Icons.bookmark_border,
                      size: 20,
                      color: _isSaved ? AppColors.accent : AppColors.textSecondary,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 10),
              Wrap(
                spacing: 8,
                runSpacing: 6,
                children: [
                  if (_salary != null)
                    MetaTag(Icons.payments_outlined, _salary!),
                  if (vacancy.city != null)
                    MetaTag(Icons.location_on_outlined, vacancy.city!),
                  if (vacancy.employmentType != null)
                    MetaTag(Icons.access_time_outlined,
                        vacancy.employmentType!.displayName),
                  if (vacancy.workFormat != null)
                    MetaTag(
                        Icons.laptop_outlined, vacancy.workFormat!.displayName),
                  if (vacancy.experienceLevel != null)
                    MetaTag(Icons.bar_chart_outlined,
                        vacancy.experienceLevel!.displayName),
                ],
              ),
              if (vacancy.skills.isNotEmpty) ...[
                const SizedBox(height: 8),
                Wrap(
                  spacing: 6,
                  runSpacing: 6,
                  children: vacancy.skills.take(5).map((s) => Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(
                          color: AppColors.tagBg,
                          borderRadius: BorderRadius.circular(999),
                        ),
                        child: Text(s,
                            style: const TextStyle(
                                fontSize: 12,
                                color: AppColors.tagText)),
                      )).toList(),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  String? get _salary =>
      formatSalary(widget.vacancy.salaryFrom, widget.vacancy.salaryTo, widget.vacancy.currency);
}

class MetaTag extends StatelessWidget {
  final IconData icon;
  final String label;

  const MetaTag(this.icon, this.label, {super.key});

  @override
  Widget build(BuildContext context) => Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: AppColors.textSecondary),
          const SizedBox(width: 4),
          Text(label,
              style:
                  const TextStyle(fontSize: 13, color: AppColors.textSecondary)),
        ],
      );
}

// ── Vacancy detail sheet ───────────────────────────────────────────────────────

class VacancyDetailSheet extends StatefulWidget {
  final Vacancy vacancy;
  final String? userRole;
  final VacancyApiService? vacancyApiService;

  /// When set, guests see a "Sign in to apply" prompt instead of the
  /// role-based apply button.
  final VoidCallback? onSignInToApply;

  const VacancyDetailSheet({
    super.key,
    required this.vacancy,
    this.userRole,
    this.vacancyApiService,
    this.onSignInToApply,
  });

  @override
  State<VacancyDetailSheet> createState() => _VacancyDetailSheetState();
}

class _VacancyDetailSheetState extends State<VacancyDetailSheet> {
  Application? _myApplication;
  Company? _company;
  bool _isApplying = false;
  bool _isSaved = false;
  bool _isSaving = false;

  bool get _isApplicant =>
      widget.userRole == 'APPLICANT' || widget.userRole == 'EMPLOYER';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final api = widget.vacancyApiService;
    if (api == null) return;
    final futures = <Future>[
      if (_isApplicant) api.getMyApplicationForVacancy(widget.vacancy.id),
      api.getCompanyById(widget.vacancy.companyId),
    ];
    final results = await Future.wait(futures);
    if (!mounted) return;
    setState(() {
      int idx = 0;
      if (_isApplicant) { _myApplication = results[idx++] as Application?; }
      _company = results[idx] as Company?;
    });
  }

  Future<void> _apply() async {
    setState(() => _isApplying = true);
    final result = await widget.vacancyApiService!.applyToVacancy(widget.vacancy.id);
    if (mounted) setState(() { _myApplication = result; _isApplying = false; });
  }

  Future<void> _withdraw() async {
    setState(() => _isApplying = true);
    final ok = await widget.vacancyApiService!.withdrawApplication(widget.vacancy.id);
    if (mounted && ok) {
      final updated = await widget.vacancyApiService!.getMyApplicationForVacancy(widget.vacancy.id);
      setState(() { _myApplication = updated; _isApplying = false; });
    } else if (mounted) {
      setState(() => _isApplying = false);
    }
  }

  Future<void> _toggleSave() async {
    final api = widget.vacancyApiService;
    if (widget.userRole == null || api == null) {
      widget.onSignInToApply?.call();
      return;
    }
    final wasSaved = _isSaved;
    setState(() {
      _isSaved = !wasSaved;
      _isSaving = true;
    });
    final ok = _isSaved
        ? await api.saveVacancy(widget.vacancy.id)
        : await api.unsaveVacancy(widget.vacancy.id);
    if (mounted) {
      setState(() {
        _isSaving = false;
        if (!ok) _isSaved = wasSaved;
      });
    }
  }

  void _viewCompany() {
    final company = _company;
    if (company == null) return;
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => CompanyDetailSheet(
        company: company,
        vacancyApiService: widget.vacancyApiService,
        userRole: widget.userRole,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final vacancy = widget.vacancy;
    return DraggableScrollableSheet(
      initialChildSize: 0.75,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, scrollCtrl) => Container(
        decoration: const BoxDecoration(
          color: AppColors.surface,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          children: [
            const BottomSheetHandle(),
            Expanded(
              child: ListView(
                controller: scrollCtrl,
                padding: const EdgeInsets.fromLTRB(24, 8, 24, 32),
                children: [
                  // Header
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: 52,
                        height: 52,
                        decoration: BoxDecoration(
                          color: AppColors.tagBg,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          (vacancy.company?.name ?? _company?.name ?? '').isNotEmpty
                              ? (vacancy.company?.name ?? _company?.name ?? '?')[0].toUpperCase()
                              : '?',
                          style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: AppColors.tagText),
                        ),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(vacancy.title,
                                style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold)),
                            const SizedBox(height: 2),
                            if ((vacancy.company?.name ?? _company?.name) != null)
                              Text(vacancy.company?.name ?? _company!.name,
                                  style: const TextStyle(
                                      fontSize: 14,
                                      color: AppColors.textSecondary)),
                            if ((vacancy.company?.city ?? _company?.city) != null)
                              Text(vacancy.company?.city ?? _company!.city!,
                                  style: const TextStyle(
                                      fontSize: 13,
                                      color: AppColors.textSecondary)),
                          ],
                        ),
                      ),
                      IconButton(
                        onPressed: _isSaving ? null : _toggleSave,
                        tooltip: _isSaved ? 'Remove from saved' : 'Save vacancy',
                        icon: Icon(
                          _isSaved ? Icons.bookmark : Icons.bookmark_border,
                          color: _isSaved ? AppColors.accent : AppColors.textSecondary,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  // Meta chips
                  Wrap(
                    spacing: 10,
                    runSpacing: 8,
                    children: [
                      if (_salary != null)
                        DetailChip(Icons.payments_outlined, _salary!,
                            color: AppColors.success),
                      if (vacancy.city != null)
                        DetailChip(Icons.location_on_outlined, vacancy.city!),
                      if (vacancy.employmentType != null)
                        DetailChip(Icons.access_time_outlined,
                            vacancy.employmentType!.displayName),
                      if (vacancy.workFormat != null)
                        DetailChip(Icons.laptop_outlined,
                            vacancy.workFormat!.displayName),
                      if (vacancy.experienceLevel != null)
                        DetailChip(Icons.bar_chart_outlined,
                            vacancy.experienceLevel!.displayName),
                    ],
                  ),
                  if (vacancy.skills.isNotEmpty) ...[
                    const SizedBox(height: 16),
                    const Text('Skills',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: vacancy.skills
                          .map((s) => Container(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: 10, vertical: 4),
                                decoration: BoxDecoration(
                                  color: AppColors.tagBg,
                                  borderRadius: BorderRadius.circular(999),
                                ),
                                child: Text(s,
                                    style: const TextStyle(
                                        fontSize: 13,
                                        color: AppColors.tagText)),
                              ))
                          .toList(),
                    ),
                  ],
                  if (vacancy.description != null &&
                      vacancy.description!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('Description',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(vacancy.description!,
                        style: const TextStyle(
                            fontSize: 14, height: 1.5, color: AppColors.text)),
                  ],
                  if (vacancy.requirements != null &&
                      vacancy.requirements!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('Requirements',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(vacancy.requirements!,
                        style: const TextStyle(
                            fontSize: 14, height: 1.5, color: AppColors.text)),
                  ],
                  // Contact info
                  if (vacancy.contactName != null ||
                      vacancy.contactEmail != null ||
                      vacancy.contactPhone != null) ...[
                    const SizedBox(height: 20),
                    const Text('Contact',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        color: AppColors.tagBg,
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: AppColors.border),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (vacancy.contactName != null)
                            ContactRow(Icons.person_outline, vacancy.contactName!),
                          if (vacancy.contactEmail != null)
                            ContactRow(Icons.email_outlined, vacancy.contactEmail!),
                          if (vacancy.contactPhone != null)
                            ContactRow(Icons.phone_outlined, vacancy.contactPhone!),
                        ],
                      ),
                    ),
                  ],
                  // Action buttons
                  const SizedBox(height: 24),
                  if (_company != null)
                    OutlinedButton.icon(
                      onPressed: _viewCompany,
                      icon: const Icon(Icons.business_outlined, size: 18),
                      label: const Text('View company'),
                      style: OutlinedButton.styleFrom(
                        minimumSize: const Size(double.infinity, 44),
                      ),
                    ),
                  if (_isApplicant) ...[
                    const SizedBox(height: 10),
                    _buildApplyButton(),
                  ] else if (widget.onSignInToApply != null) ...[
                    const SizedBox(height: 10),
                    _buildSignInButton(),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSignInButton() {
    return FilledButton.icon(
      onPressed: widget.onSignInToApply,
      icon: const Icon(Icons.login, size: 18),
      label: const Text('Sign in to apply'),
      style: FilledButton.styleFrom(
        minimumSize: const Size(double.infinity, 44),
      ),
    );
  }

  Widget _buildApplyButton() {
    final app = _myApplication;
    if (app == null) {
      return FilledButton.icon(
        onPressed: _isApplying ? null : _apply,
        icon: _isApplying
            ? const SizedBox(width: 16, height: 16,
                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
            : const Icon(Icons.send_outlined, size: 18),
        label: const Text('Apply'),
        style: FilledButton.styleFrom(
          minimumSize: const Size(double.infinity, 44),
        ),
      );
    }
    final statusColor = switch (app.status) {
      ApplicationStatus.ACCEPTED => AppColors.success,
      ApplicationStatus.REJECTED => AppColors.error,
      ApplicationStatus.WITHDRAWN => AppColors.textSecondary,
      _ => AppColors.primary,
    };
    final canWithdraw = app.status.isActive;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(
            color: statusColor.withAlpha(20),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: statusColor.withAlpha(80)),
          ),
          child: Row(
            children: [
              Icon(Icons.info_outline, size: 16, color: statusColor),
              const SizedBox(width: 8),
              Text('Application status: ${app.status.displayName}',
                  style: TextStyle(color: statusColor, fontWeight: FontWeight.w500)),
            ],
          ),
        ),
        if (canWithdraw) ...[
          const SizedBox(height: 8),
          OutlinedButton.icon(
            onPressed: _isApplying ? null : _withdraw,
            icon: _isApplying
                ? const SizedBox(width: 16, height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2))
                : const Icon(Icons.close, size: 18),
            label: const Text('Withdraw application'),
            style: OutlinedButton.styleFrom(
              foregroundColor: AppColors.error,
              side: const BorderSide(color: AppColors.error),
              minimumSize: const Size(double.infinity, 44),
            ),
          ),
        ],
      ],
    );
  }

  String? get _salary {
    final v = widget.vacancy;
    return formatSalary(v.salaryFrom, v.salaryTo, v.currency);
  }
}

class DetailChip extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color? color;

  const DetailChip(this.icon, this.label, {super.key, this.color});

  @override
  Widget build(BuildContext context) {
    final c = color ?? AppColors.textSecondary;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: c.withAlpha(20),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 15, color: c),
          const SizedBox(width: 5),
          Text(label, style: TextStyle(fontSize: 13, color: c)),
        ],
      ),
    );
  }
}

class ContactRow extends StatelessWidget {
  final IconData icon;
  final String text;
  const ContactRow(this.icon, this.text, {super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 6),
      child: Row(children: [
        Icon(icon, size: 16, color: AppColors.primary),
        const SizedBox(width: 8),
        Expanded(child: Text(text,
            style: const TextStyle(fontSize: 14, color: AppColors.text))),
      ]),
    );
  }
}

// ── Company detail sheet ───────────────────────────────────────────────────────

class CompanyDetailSheet extends StatefulWidget {
  final Company company;
  final VacancyApiService? vacancyApiService;
  final String? userRole;

  const CompanyDetailSheet({
    super.key,
    required this.company,
    this.vacancyApiService,
    this.userRole,
  });

  @override
  State<CompanyDetailSheet> createState() => _CompanyDetailSheetState();
}

class _CompanyDetailSheetState extends State<CompanyDetailSheet> {
  List<CompanyReview> _reviews = [];
  bool _isLoadingReviews = true;

  bool get _canReview =>
      widget.userRole == 'APPLICANT' || widget.userRole == 'EMPLOYER';

  @override
  void initState() {
    super.initState();
    _loadReviews();
  }

  Future<void> _loadReviews() async {
    final api = widget.vacancyApiService;
    if (api == null) {
      setState(() => _isLoadingReviews = false);
      return;
    }
    final reviews = await api.getCompanyReviews(widget.company.id);
    if (mounted) setState(() { _reviews = reviews; _isLoadingReviews = false; });
  }

  Future<void> _openReviewForm() async {
    final result = await showDialog<(int, String?)>(
      context: context,
      builder: (_) => const _ReviewFormDialog(),
    );
    if (result == null) return;
    final review = await widget.vacancyApiService!
        .submitCompanyReview(widget.company.id, result.$1, result.$2);
    if (review != null && mounted) {
      setState(() => _reviews = [review, ..._reviews]);
    }
  }

  @override
  Widget build(BuildContext context) {
    final company = widget.company;
    return DraggableScrollableSheet(
      initialChildSize: 0.65,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, scrollCtrl) => Container(
        decoration: const BoxDecoration(
          color: AppColors.surface,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          children: [
            const BottomSheetHandle(),
            Expanded(
              child: ListView(
                controller: scrollCtrl,
                padding: const EdgeInsets.fromLTRB(24, 8, 24, 32),
                children: [
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: 56, height: 56,
                        decoration: BoxDecoration(
                          color: AppColors.tagBg,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          company.name.isNotEmpty
                              ? company.name[0].toUpperCase()
                              : '?',
                          style: const TextStyle(
                              fontSize: 26,
                              fontWeight: FontWeight.bold,
                              color: AppColors.tagText),
                        ),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(company.name,
                                style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold)),
                            if (company.industry != null)
                              Text(company.industry!,
                                  style: const TextStyle(
                                      fontSize: 14,
                                      color: AppColors.textSecondary)),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Wrap(
                    spacing: 10,
                    runSpacing: 8,
                    children: [
                      if (company.city != null)
                        DetailChip(Icons.location_on_outlined, company.city!),
                      if (company.country != null)
                        DetailChip(Icons.flag_outlined, company.country!),
                      if (company.size != null)
                        DetailChip(
                            Icons.people_outline, company.size!.displayName),
                      DetailChip(
                        Icons.star_outline,
                        '${company.rating.toStringAsFixed(1)} (${company.reviewCount} reviews)',
                        color: AppColors.warning,
                      ),
                    ],
                  ),
                  if (company.website != null &&
                      company.website!.isNotEmpty) ...[
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        const Icon(Icons.link,
                            size: 16, color: AppColors.textSecondary),
                        const SizedBox(width: 6),
                        Text(company.website!,
                            style: const TextStyle(
                                fontSize: 14, color: AppColors.primary)),
                      ],
                    ),
                  ],
                  if (company.description != null &&
                      company.description!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('About',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(company.description!,
                        style: const TextStyle(
                            fontSize: 14,
                            height: 1.5,
                            color: AppColors.text)),
                  ],
                  const SizedBox(height: 24),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Reviews',
                          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                      if (widget.vacancyApiService != null && _canReview)
                        TextButton.icon(
                          onPressed: _openReviewForm,
                          icon: const Icon(Icons.add, size: 16),
                          label: const Text('Leave a review'),
                          style: TextButton.styleFrom(
                              padding: EdgeInsets.zero,
                              minimumSize: const Size(0, 0),
                              tapTargetSize: MaterialTapTargetSize.shrinkWrap),
                        ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  if (_isLoadingReviews)
                    const Center(
                        child: Padding(
                      padding: EdgeInsets.symmetric(vertical: 16),
                      child: SizedBox(
                          width: 20, height: 20,
                          child: CircularProgressIndicator(strokeWidth: 2)),
                    ))
                  else if (_reviews.isEmpty)
                    const Text('No reviews yet',
                        style: TextStyle(fontSize: 13, color: AppColors.textSecondary))
                  else
                    ..._reviews.map((r) => Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: AppColors.bg,
                              borderRadius: BorderRadius.circular(10),
                              border: Border.all(color: AppColors.border),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: List.generate(
                                    5,
                                    (i) => Icon(
                                      i < r.rating ? Icons.star : Icons.star_border,
                                      size: 15,
                                      color: AppColors.warning,
                                    ),
                                  ),
                                ),
                                if (r.comment != null && r.comment!.isNotEmpty) ...[
                                  const SizedBox(height: 6),
                                  Text(r.comment!,
                                      style: const TextStyle(fontSize: 13, height: 1.4)),
                                ],
                              ],
                            ),
                          ),
                        )),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ReviewFormDialog extends StatefulWidget {
  const _ReviewFormDialog();

  @override
  State<_ReviewFormDialog> createState() => _ReviewFormDialogState();
}

class _ReviewFormDialogState extends State<_ReviewFormDialog> {
  int _rating = 5;
  final _commentCtrl = TextEditingController();

  @override
  void dispose() {
    _commentCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Leave a review'),
      content: SizedBox(
        width: 360,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(5, (i) {
                final filled = i < _rating;
                return IconButton(
                  onPressed: () => setState(() => _rating = i + 1),
                  icon: Icon(filled ? Icons.star : Icons.star_border,
                      color: AppColors.warning),
                );
              }),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _commentCtrl,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'Comment (optional)',
              ),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancel'),
        ),
        FilledButton(
          onPressed: () => Navigator.pop(
              context, (_rating, _commentCtrl.text.trim())),
          child: const Text('Submit'),
        ),
      ],
    );
  }
}
