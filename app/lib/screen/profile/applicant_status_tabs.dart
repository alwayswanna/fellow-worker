import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../cubit/applications/applications_cubit.dart';
import '../../cubit/applications/applications_state.dart';
import '../../cubit/saved_vacancies/saved_vacancies_cubit.dart';
import '../../cubit/saved_vacancies/saved_vacancies_state.dart';
import '../../model/application.dart';
import '../../model/vacancy.dart';
import '../../service/vacancy_api_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/widgets.dart';

/// "My Applications" tab inside [ApplicantTabPanel] — reads [ApplicationsCubit].
class MyApplicationsTab extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;

  const MyApplicationsTab({
    super.key,
    required this.vacancyApiService,
    this.userRole,
  });

  @override
  State<MyApplicationsTab> createState() => _MyApplicationsTabState();
}

class _MyApplicationsTabState extends State<MyApplicationsTab> {
  String? _loadingVacancyId;

  Future<void> _openVacancy(Application app, Vacancy? cachedVacancy) async {
    var vacancy = cachedVacancy;
    if (vacancy == null) {
      setState(() => _loadingVacancyId = app.vacancyId);
      vacancy = await widget.vacancyApiService.getVacancyById(app.vacancyId);
      if (!mounted) return;
      setState(() => _loadingVacancyId = null);
    }
    final resolvedVacancy = vacancy;
    if (resolvedVacancy == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('This vacancy is no longer available')),
      );
      return;
    }
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => VacancyDetailSheet(
        vacancy: resolvedVacancy,
        vacancyApiService: widget.vacancyApiService,
        userRole: widget.userRole,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<ApplicationsCubit, ApplicationsState>(
      builder: (context, state) {
        if (state.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }
        if (state.applications.isEmpty) {
          return const EmptyState(
            icon: Icons.send_outlined,
            title: 'No applications yet',
            subtitle: 'Browse vacancies and apply to get started',
          );
        }
        final cubit = context.read<ApplicationsCubit>();
        return RefreshIndicator(
          onRefresh: cubit.load,
          child: ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: state.applications.length,
            separatorBuilder: (_, __) => const SizedBox(height: 8),
            itemBuilder: (_, i) {
              final app = state.applications[i];
              final vacancy = state.vacanciesById[app.vacancyId];
              final title = vacancy?.title ??
                  'Vacancy ${app.vacancyId.substring(0, 8)}…';
              final color = StatusBadge.colorFor(app.status);
              final isLoading = _loadingVacancyId == app.vacancyId;
              return Material(
                color: AppColors.surface,
                borderRadius: BorderRadius.circular(10),
                child: InkWell(
                  borderRadius: BorderRadius.circular(10),
                  onTap: isLoading ? null : () => _openVacancy(app, vacancy),
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(10),
                      border: Border.all(color: AppColors.border),
                    ),
                    padding: const EdgeInsets.all(14),
                    child: Row(children: [
                      Container(
                        width: 40, height: 40,
                        decoration: BoxDecoration(color: color.withAlpha(30),
                            borderRadius: BorderRadius.circular(8)),
                        child: Icon(Icons.work_outline, color: color, size: 20),
                      ),
                      const SizedBox(width: 12),
                      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                        Text(title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                        const SizedBox(height: 4),
                        StatusBadge(app.status),
                      ])),
                      if (isLoading)
                        const SizedBox(
                          width: 20, height: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      else if (app.status.isActive)
                        IconButton(
                          icon: Icon(Icons.close, color: AppColors.error, size: 20),
                          tooltip: 'Withdraw',
                          onPressed: () => cubit.withdraw(app),
                        )
                      else
                        Icon(Icons.chevron_right, size: 20, color: AppColors.border),
                    ]),
                  ),
                ),
              );
            },
          ),
        );
      },
    );
  }
}

/// "Saved" tab inside [ApplicantTabPanel] — reads [SavedVacanciesCubit].
class SavedVacanciesTab extends StatelessWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;

  const SavedVacanciesTab({super.key, required this.vacancyApiService, this.userRole});

  void _showVacancyDetail(BuildContext context, Vacancy v) {
    final cubit = context.read<SavedVacanciesCubit>();
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => VacancyDetailSheet(
        vacancy: v,
        vacancyApiService: vacancyApiService,
        userRole: userRole,
      ),
    ).then((_) => cubit.load());
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<SavedVacanciesCubit, SavedVacanciesState>(
      builder: (context, state) {
        if (state.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }
        if (state.vacancies.isEmpty) {
          return const EmptyState(
            icon: Icons.bookmark_border,
            title: 'No saved vacancies',
            subtitle: 'Bookmark vacancies you like to find them here later',
          );
        }
        return RefreshIndicator(
          onRefresh: context.read<SavedVacanciesCubit>().load,
          child: ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: state.vacancies.length,
            separatorBuilder: (_, __) => const SizedBox(height: 10),
            itemBuilder: (_, i) => VacancyBrowseCard(
              vacancy: state.vacancies[i],
              onTap: () => _showVacancyDetail(context, state.vacancies[i]),
              vacancyApiService: vacancyApiService,
              canSave: true,
              initiallySaved: true,
            ),
          ),
        );
      },
    );
  }
}
