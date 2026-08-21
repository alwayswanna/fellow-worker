import 'package:flutter/material.dart';

import '../../../model/vacancy.dart';
import '../../../service/vacancy_api_service.dart';
import '../../../theme/app_theme.dart';
import '../../../util/dialogs.dart';
import '../../../widgets/widgets.dart';
import 'vacancy_form_dialog.dart';

/// Vacancies list for a company — shared by [CompanyOwnerPanel] (its own
/// company) and [RecruiterPanel] (the company it recruits for).
class VacanciesTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;

  const VacanciesTab({super.key, required this.company, required this.vacancyApiService});

  @override
  State<VacanciesTab> createState() => _VacanciesTabState();
}

class _VacanciesTabState extends State<VacanciesTab> {
  List<Vacancy> _vacancies = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.company != null) _load();
  }

  Future<void> _load() async {
    setState(() => _isLoading = true);
    final results = await Future.wait([
      widget.vacancyApiService.searchVacancies(
          companyId: widget.company!.id, status: VacancyStatus.ACTIVE),
      widget.vacancyApiService.searchVacancies(
          companyId: widget.company!.id, status: VacancyStatus.DRAFT),
    ]);
    if (mounted) {
      setState(() {
        _vacancies = [...results[0], ...results[1]];
        _isLoading = false;
      });
    }
  }

  Future<void> _showCreate() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => VacancyFormDialog(
          companyId: widget.company!.id, vacancyApiService: widget.vacancyApiService),
    );
    if (ok == true) _load();
  }

  Future<void> _showEdit(Vacancy vacancy) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => VacancyFormDialog(
          companyId: widget.company!.id,
          vacancyApiService: widget.vacancyApiService,
          existing: vacancy),
    );
    if (ok == true) _load();
  }

  Future<void> _delete(Vacancy vacancy) async {
    final confirmed = await confirmDialog(context,
        title: 'Delete vacancy', message: 'Delete "${vacancy.title}"?');
    if (!confirmed) return;
    await widget.vacancyApiService.deleteVacancy(vacancy.id);
    if (mounted) _load();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.company == null) {
      return Center(child: Padding(padding: EdgeInsets.all(32),
          child: Text('Create your company first to manage vacancies.',
              textAlign: TextAlign.center,
              style: TextStyle(color: AppColors.textSecondary))));
    }
    return Column(children: [
      Container(
        color: AppColors.surface,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          const Text('Vacancies', style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
          FilledButton.icon(
            onPressed: _showCreate,
            icon: const Icon(Icons.add, size: 16),
            label: const Text('New vacancy'),
            style: FilledButton.styleFrom(backgroundColor: AppColors.accent,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          ),
        ]),
      ),
      Divider(height: 1, color: AppColors.border),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _vacancies.isEmpty
                ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(Icons.work_off_outlined, size: 48, color: AppColors.border),
                    const SizedBox(height: 12),
                    Text('No vacancies yet',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600,
                            color: AppColors.text)),
                  ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _vacancies.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (_, i) {
                      final vacancy = _vacancies[i];
                      return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                        VacancyBrowseCard(
                          vacancy: vacancy,
                          onTap: () => showModalBottomSheet(
                            context: context, isScrollControlled: true,
                            backgroundColor: Colors.transparent,
                            builder: (_) => VacancyDetailSheet(
                              vacancy: vacancy,
                              vacancyApiService: widget.vacancyApiService,
                            ),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.only(top: 4),
                          child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
                            TextButton.icon(
                              onPressed: () => _showEdit(vacancy),
                              icon: const Icon(Icons.edit_outlined, size: 16),
                              label: const Text('Edit'),
                            ),
                            TextButton.icon(
                              onPressed: () => _delete(vacancy),
                              icon: Icon(Icons.delete_outline, size: 16, color: AppColors.error),
                              label: Text('Delete', style: TextStyle(color: AppColors.error)),
                            ),
                          ]),
                        ),
                      ]);
                    },
                  ),
      ),
    ]);
  }
}
