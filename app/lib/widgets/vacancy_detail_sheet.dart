import 'package:flutter/material.dart';

import '../model/application.dart';
import '../model/vacancy.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import '../util/formatters.dart';
import 'bottom_sheet_handle.dart';
import 'company_detail_sheet.dart';
import 'detail_chip.dart';
import 'gradient_button.dart';
import 'initials_avatar.dart';

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

  bool get _isApplicant => widget.userRole == 'APPLICANT';

  bool get _canToggleSave =>
      (widget.userRole != null && widget.vacancyApiService != null) ||
      widget.onSignInToApply != null;

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
        decoration: BoxDecoration(
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
                      InitialsAvatar(
                        name: vacancy.company?.name ?? _company?.name ?? '',
                        radius: 26,
                        borderRadius: 12,
                        color: AppColors.avatarColor(
                                vacancy.company?.name ?? _company?.name ?? '')
                            .withAlpha(38),
                        textColor: AppColors.avatarColor(
                            vacancy.company?.name ?? _company?.name ?? ''),
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
                                  style: TextStyle(
                                      fontSize: 14,
                                      color: AppColors.textSecondary)),
                            if ((vacancy.company?.city ?? _company?.city) != null)
                              Text(vacancy.company?.city ?? _company!.city!,
                                  style: TextStyle(
                                      fontSize: 13,
                                      color: AppColors.textSecondary)),
                          ],
                        ),
                      ),
                      if (_canToggleSave)
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
                                    style: TextStyle(
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
                        style: TextStyle(
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
                        style: TextStyle(
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
    return GradientButton(
      onPressed: widget.onSignInToApply,
      icon: const Icon(Icons.login, size: 18),
      label: const Text('Sign in to apply'),
    );
  }

  Widget _buildApplyButton() {
    final app = _myApplication;
    if (app == null) {
      return GradientButton(
        onPressed: _isApplying ? null : _apply,
        icon: _isApplying
            ? const SizedBox(width: 16, height: 16,
                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
            : const Icon(Icons.send_outlined, size: 18),
        label: const Text('Apply'),
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
              side: BorderSide(color: AppColors.error),
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
