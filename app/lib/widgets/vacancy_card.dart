import 'package:flutter/material.dart';

import '../model/vacancy.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import '../util/formatters.dart';
import 'initials_avatar.dart';

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
      color: Colors.transparent,
      borderRadius: BorderRadius.circular(16),
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: widget.onTap,
        child: Container(
          decoration: BoxDecoration(
            color: AppColors.surface,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppColors.border),
            boxShadow: [
              BoxShadow(
                color: AppColors.cardShadow,
                blurRadius: 16,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  InitialsAvatar(
                    name: vacancy.company?.name ?? '',
                    radius: 20,
                    borderRadius: 8,
                    color: AppColors.avatarColor(vacancy.company?.name ?? '').withAlpha(38),
                    textColor: AppColors.avatarColor(vacancy.company?.name ?? ''),
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
                            style: TextStyle(
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
                            style: TextStyle(
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
                  TextStyle(fontSize: 13, color: AppColors.textSecondary)),
        ],
      );
}
