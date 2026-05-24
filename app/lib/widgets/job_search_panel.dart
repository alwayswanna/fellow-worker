import 'package:flutter/material.dart';

import '../model/vacancy.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import 'empty_state.dart';
import 'vacancy_card.dart';
import 'vacancy_detail_sheet.dart';

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
                  prefixIcon: Icon(Icons.search, color: AppColors.textSecondary),
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
                          child: Text('Clear all',
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
