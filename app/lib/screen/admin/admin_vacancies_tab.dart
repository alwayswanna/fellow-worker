import 'package:flutter/material.dart';

import '../../model/vacancy.dart';
import '../../service/http_client.dart';
import '../../service/vacancy_api_service.dart';
import '../../util/dialogs.dart';
import '../../util/formatters.dart';
import '../../util/paginated_list_controller.dart';
import '../../theme/app_theme.dart';
import 'admin_vacancies/vacancy_form_dialog.dart';

class AdminVacanciesTab extends StatefulWidget {
  final VacancyApiService vacancyApiService;

  const AdminVacanciesTab({super.key, required this.vacancyApiService});

  @override
  State<AdminVacanciesTab> createState() => _AdminVacanciesTabState();
}

class _AdminVacanciesTabState extends State<AdminVacanciesTab>
    with PaginatedListMixin<Vacancy, AdminVacanciesTab> {
  final TextEditingController _searchController = TextEditingController();
  VacancyStatus _statusFilter = VacancyStatus.ACTIVE;
  static const int _pageSize = 20;

  @override
  void initState() {
    super.initState();
    initPagination();
  }

  @override
  void dispose() {
    disposePagination();
    _searchController.dispose();
    super.dispose();
  }

  @override
  Future<(List<Vacancy>, bool)> fetchPage(int page) async {
    final results = await widget.vacancyApiService.searchVacancies(
      title: _searchController.text.trim().isEmpty
          ? null
          : _searchController.text.trim(),
      status: _statusFilter,
      page: page,
      size: _pageSize,
    );
    return (results, results.length >= _pageSize);
  }

  Future<void> _showAddDialog() async {
    final result = await showDialog<VacancyFormResult>(
      context: context,
      builder: (_) => const VacancyFormDialog(),
    );
    if (result == null || !mounted) return;

    final body = <String, dynamic>{
      'title': result.title,
      'companyId': result.companyId,
      'status': result.status.name,
      if (result.description.isNotEmpty) 'description': result.description,
      if (result.requirements.isNotEmpty) 'requirements': result.requirements,
      if (result.salaryFrom.isNotEmpty)
        'salaryFrom': int.tryParse(result.salaryFrom),
      if (result.salaryTo.isNotEmpty) 'salaryTo': int.tryParse(result.salaryTo),
      if (result.currency.isNotEmpty) 'currency': result.currency,
      if (result.employmentType != null)
        'employmentType': result.employmentType!.name,
      if (result.workFormat != null) 'workFormat': result.workFormat!.name,
      if (result.experienceLevel != null)
        'experienceLevel': result.experienceLevel!.name,
      if (result.city.isNotEmpty) 'city': result.city,
      if (result.country.isNotEmpty) 'country': result.country,
      if (result.skills.isNotEmpty) 'skills': result.skills,
      if (result.contactName.isNotEmpty) 'contactName': result.contactName,
      if (result.contactEmail.isNotEmpty) 'contactEmail': result.contactEmail,
      if (result.contactPhone.isNotEmpty) 'contactPhone': result.contactPhone,
    };

    try {
      await widget.vacancyApiService.createVacancy(body);
      if (mounted) await loadPage(reset: true);
    } catch (e) {
      if (mounted) {
        _showSnack(e is ApiException ? e.message : 'Failed to create vacancy');
      }
    }
  }

  Future<void> _showEditDialog(Vacancy vacancy) async {
    final result = await showDialog<VacancyFormResult>(
      context: context,
      builder: (_) => VacancyFormDialog(existing: vacancy),
    );
    if (result == null || !mounted) return;

    final body = <String, dynamic>{
      'title': result.title,
      'status': result.status.name,
      'description': result.description.isEmpty ? null : result.description,
      'requirements': result.requirements.isEmpty ? null : result.requirements,
      'salaryFrom': int.tryParse(result.salaryFrom),
      'salaryTo': int.tryParse(result.salaryTo),
      'currency': result.currency.isEmpty ? null : result.currency,
      'employmentType': result.employmentType?.name,
      'workFormat': result.workFormat?.name,
      'experienceLevel': result.experienceLevel?.name,
      'city': result.city.isEmpty ? null : result.city,
      'country': result.country.isEmpty ? null : result.country,
      'skills': result.skills,
      'contactName': result.contactName.isEmpty ? null : result.contactName,
      'contactEmail': result.contactEmail.isEmpty ? null : result.contactEmail,
      'contactPhone': result.contactPhone.isEmpty ? null : result.contactPhone,
    };

    try {
      await widget.vacancyApiService.updateVacancy(vacancy.id, body);
      if (mounted) await loadPage(reset: true);
    } catch (e) {
      if (mounted) {
        _showSnack(e is ApiException ? e.message : 'Failed to update vacancy');
      }
    }
  }

  Future<void> _delete(Vacancy vacancy) async {
    final confirmed = await confirmDialog(context,
        title: 'Delete vacancy', message: 'Delete "${vacancy.title}"?');
    if (!confirmed) return;
    await widget.vacancyApiService.deleteVacancy(vacancy.id);
    if (mounted) await loadPage(reset: true);
  }

  void _showSnack(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

  Color _statusColor(VacancyStatus s) => switch (s) {
        VacancyStatus.ACTIVE => AppColors.success,
        VacancyStatus.DRAFT => AppColors.warning,
        VacancyStatus.CLOSED => AppColors.textSecondary,
      };

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _searchController,
                  decoration: InputDecoration(
                    hintText: 'Search by title…',
                    prefixIcon: const Icon(Icons.search),
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8)),
                    contentPadding: const EdgeInsets.symmetric(vertical: 0),
                    suffixIcon: _searchController.text.isNotEmpty
                        ? IconButton(
                            icon: const Icon(Icons.clear),
                            onPressed: () {
                              _searchController.clear();
                              loadPage(reset: true);
                            },
                          )
                        : null,
                  ),
                  onSubmitted: (_) => loadPage(reset: true),
                ),
              ),
              const SizedBox(width: 8),
              DropdownButton<VacancyStatus>(
                value: _statusFilter,
                underline: const SizedBox(),
                items: VacancyStatus.values
                    .map((s) => DropdownMenuItem(
                        value: s, child: Text(s.displayName)))
                    .toList(),
                onChanged: (v) {
                  if (v != null) {
                    setState(() => _statusFilter = v);
                    loadPage(reset: true);
                  }
                },
              ),
              const SizedBox(width: 8),
              FilledButton.icon(
                style: FilledButton.styleFrom(backgroundColor: AppColors.accent),
                icon: const Icon(Icons.add),
                label: const Text('Add'),
                onPressed: _showAddDialog,
              ),
            ],
          ),
        ),
        Expanded(
          child: isLoading
              ? const Center(child: CircularProgressIndicator())
              : items.isEmpty
                  ? const Center(child: Text('No vacancies found'))
                  : RefreshIndicator(
                      onRefresh: () => loadPage(reset: true),
                      child: ListView.separated(
                        controller: scrollController,
                        itemCount:
                            items.length + (isLoadingMore ? 1 : 0),
                        separatorBuilder: (_, __) =>
                            const Divider(height: 1),
                        itemBuilder: (_, i) {
                          if (i == items.length) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 16),
                              child:
                                  Center(child: CircularProgressIndicator()),
                            );
                          }
                          final v = items[i];
                          return ListTile(
                            leading: CircleAvatar(
                              backgroundColor: AppColors.tagBg,
                              child: Icon(Icons.work_outline,
                                  color: AppColors.primary),
                            ),
                            title: Text(v.title),
                            subtitle: Text([
                              if (v.company != null) v.company!.name,
                              if (v.city != null) v.city!,
                              if (v.employmentType != null)
                                v.employmentType!.displayName,
                              if (v.salaryFrom != null || v.salaryTo != null)
                                formatSalary(v.salaryFrom, v.salaryTo,
                                    v.currency ?? 'RUB'),
                            ].join(' · ')),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Container(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 8, vertical: 2),
                                  decoration: BoxDecoration(
                                    color: _statusColor(v.status)
                                        .withOpacity(0.1),
                                    borderRadius: BorderRadius.circular(12),
                                    border: Border.all(
                                        color: _statusColor(v.status)
                                            .withOpacity(0.4)),
                                  ),
                                  child: Text(
                                    v.status.displayName,
                                    style: TextStyle(
                                        fontSize: 11,
                                        color: _statusColor(v.status)),
                                  ),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.edit_outlined),
                                  tooltip: 'Edit',
                                  onPressed: () => _showEditDialog(v),
                                ),
                                IconButton(
                                  icon: Icon(Icons.delete_outline,
                                      color: AppColors.error),
                                  tooltip: 'Delete',
                                  onPressed: () => _delete(v),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
        ),
      ],
    );
  }
}
