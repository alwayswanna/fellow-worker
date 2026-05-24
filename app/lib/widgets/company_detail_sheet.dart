import 'package:flutter/material.dart';

import '../model/vacancy.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import 'bottom_sheet_handle.dart';
import 'detail_chip.dart';
import 'initials_avatar.dart';

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

  bool get _canReview => widget.userRole == 'APPLICANT';

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
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      InitialsAvatar(
                        name: company.name,
                        radius: 28,
                        borderRadius: 12,
                        color: AppColors.avatarColor(company.name).withAlpha(38),
                        textColor: AppColors.avatarColor(company.name),
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
                                  style: TextStyle(
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
                        Icon(Icons.link,
                            size: 16, color: AppColors.textSecondary),
                        const SizedBox(width: 6),
                        Text(company.website!,
                            style: TextStyle(
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
                        style: TextStyle(
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
                    Text('No reviews yet',
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
