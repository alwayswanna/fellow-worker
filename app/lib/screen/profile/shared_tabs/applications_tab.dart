import 'package:flutter/material.dart';

import '../../../model/application.dart';
import '../../../model/user_profile.dart';
import '../../../model/vacancy.dart';
import '../../../service/user_api_service.dart';
import '../../../service/vacancy_api_service.dart';
import '../../../theme/app_theme.dart';
import '../../../widgets/widgets.dart';

/// Per-vacancy application counts/list for a company — shared by
/// [CompanyOwnerPanel] and [RecruiterPanel].
class RecruiterApplicationsTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const RecruiterApplicationsTab({
    super.key,
    required this.company,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<RecruiterApplicationsTab> createState() =>
      _RecruiterApplicationsTabState();
}

class _RecruiterApplicationsTabState extends State<RecruiterApplicationsTab> {
  List<Vacancy> _vacancies = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final companyId = widget.company?.id;
    if (companyId == null) return;
    setState(() => _isLoading = true);
    final results = await Future.wait([
      widget.vacancyApiService.searchVacancies(
          companyId: companyId, status: VacancyStatus.ACTIVE),
      widget.vacancyApiService.searchVacancies(
          companyId: companyId, status: VacancyStatus.DRAFT),
    ]);
    if (mounted) {
      setState(() {
        _vacancies = [...results[0], ...results[1]];
        _isLoading = false;
      });
    }
  }

  void _showApplications(Vacancy vacancy) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => VacancyApplicationsSheet(
        vacancy: vacancy,
        vacancyApiService: widget.vacancyApiService,
        userApiService: widget.userApiService,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    if (_vacancies.isEmpty) {
      return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(Icons.inbox_outlined, size: 48, color: AppColors.border),
        const SizedBox(height: 12),
        Text('No vacancies yet',
            style: TextStyle(fontSize: 15, color: AppColors.text)),
      ]));
    }
    return ListView.separated(
      padding: const EdgeInsets.all(16),
      itemCount: _vacancies.length,
      separatorBuilder: (_, __) => const SizedBox(height: 8),
      itemBuilder: (_, i) {
        final v = _vacancies[i];
        return Material(
          color: AppColors.surface,
          borderRadius: BorderRadius.circular(10),
          child: ListTile(
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            title: Text(v.title,
                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
            subtitle: Text(v.status.displayName,
                style: TextStyle(fontSize: 12,
                    color: v.status == VacancyStatus.ACTIVE ? AppColors.success : AppColors.textSecondary)),
            trailing: FilledButton.icon(
              onPressed: () => _showApplications(v),
              icon: const Icon(Icons.people_outline, size: 16),
              label: const Text('Applications'),
              style: FilledButton.styleFrom(
                backgroundColor: AppColors.accent,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ),
        );
      },
    );
  }
}

class VacancyApplicationsSheet extends StatefulWidget {
  final Vacancy vacancy;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const VacancyApplicationsSheet({
    super.key,
    required this.vacancy,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<VacancyApplicationsSheet> createState() =>
      _VacancyApplicationsSheetState();
}

class _VacancyApplicationsSheetState
    extends State<VacancyApplicationsSheet> {
  List<({Application app, UserShort? user})> _entries = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _isLoading = true);
    final apps =
        await widget.vacancyApiService.getVacancyApplications(widget.vacancy.id);
    final entries = await Future.wait(apps.map((a) async {
      final user = await widget.userApiService.getUserById(a.applicantAccountId);
      return (app: a, user: user);
    }));
    if (mounted) setState(() { _entries = entries; _isLoading = false; });
  }

  Future<void> _changeStatus(
      Application app, ApplicationStatus newStatus) async {
    await widget.vacancyApiService
        .updateApplicationStatus(widget.vacancy.id, app.id, newStatus);
    _load();
  }

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.75,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, ctrl) => Container(
        decoration: BoxDecoration(
          color: AppColors.surface,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(children: [
          const BottomSheetHandle(),
          Padding(
            padding: const EdgeInsets.fromLTRB(24, 8, 24, 12),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('Applications',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              Text(widget.vacancy.title,
                  style: TextStyle(fontSize: 13, color: AppColors.textSecondary)),
            ]),
          ),
          const Divider(height: 1),
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _entries.isEmpty
                    ? const EmptyState(
                        icon: Icons.people_outline,
                        title: 'No applications yet',
                      )
                    : ListView.separated(
                        controller: ctrl,
                        padding: const EdgeInsets.all(16),
                        itemCount: _entries.length,
                        separatorBuilder: (_, __) => const SizedBox(height: 8),
                        itemBuilder: (_, i) {
                          final entry = _entries[i];
                          final app = entry.app;
                          final user = entry.user;
                          final canAct = app.status != ApplicationStatus.WITHDRAWN &&
                              app.status != ApplicationStatus.REJECTED &&
                              app.status != ApplicationStatus.ACCEPTED;
                          return Material(
                            color: AppColors.surface,
                            borderRadius: BorderRadius.circular(10),
                            elevation: 0.5,
                            child: Padding(
                              padding: const EdgeInsets.all(12),
                              child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                Row(children: [
                                  InitialsAvatar(
                                    name: user?.firstName ?? '?',
                                    color: AppColors.tagBg,
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                      child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                        Text(user?.fullName ?? 'Unknown',
                                            style: const TextStyle(
                                                fontWeight: FontWeight.w600,
                                                fontSize: 14)),
                                        Text('@${user?.login ?? '…'}',
                                            style: TextStyle(
                                                fontSize: 12,
                                                color: AppColors.textSecondary)),
                                      ])),
                                  StatusBadge(app.status),
                                ]),
                                if (canAct) ...[
                                  const SizedBox(height: 10),
                                  Row(children: [
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.REVIEWED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: AppColors.warning,
                                        side: BorderSide(
                                            color: AppColors.warning),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Reviewed',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                    const SizedBox(width: 8),
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.ACCEPTED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: AppColors.success,
                                        side: BorderSide(
                                            color: AppColors.success),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Accept',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                    const SizedBox(width: 8),
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.REJECTED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: AppColors.error,
                                        side: BorderSide(color: AppColors.error),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Reject',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                  ]),
                                ],
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
