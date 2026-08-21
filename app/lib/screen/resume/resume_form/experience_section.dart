import 'package:flutter/material.dart';

import '../../../theme/app_theme.dart';
import 'form_entries.dart';
import 'form_widgets.dart';

class ExperienceSection extends StatelessWidget {
  final List<ExpEntry> entries;
  final ValueChanged<int> onRemove;
  final VoidCallback onAdd;
  final Future<void> Function(TextEditingController) onPickDate;

  const ExperienceSection({
    super.key,
    required this.entries,
    required this.onRemove,
    required this.onAdd,
    required this.onPickDate,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...entries.asMap().entries.map((e) => ExperienceCard(
              entry: e.value,
              index: e.key,
              onRemove: () => onRemove(e.key),
              onPickDate: onPickDate,
            )),
        AddEntryButton(label: 'Add work experience', onTap: onAdd),
      ],
    );
  }
}

class ExperienceCard extends StatelessWidget {
  final ExpEntry entry;
  final int index;
  final VoidCallback onRemove;
  final Future<void> Function(TextEditingController) onPickDate;

  const ExperienceCard({
    super.key,
    required this.entry,
    required this.index,
    required this.onRemove,
    required this.onPickDate,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: AppColors.bg,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.border),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 28,
                height: 28,
                decoration: BoxDecoration(
                  color: AppColors.tagBg,
                  shape: BoxShape.circle,
                ),
                alignment: Alignment.center,
                child: Text('${index + 1}',
                    style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: AppColors.tagText)),
              ),
              const SizedBox(width: 8),
              Text('Experience ${index + 1}',
                  style: TextStyle(
                      fontWeight: FontWeight.w600, fontSize: 14,
                      color: AppColors.text)),
              const Spacer(),
              GestureDetector(
                onTap: onRemove,
                child: Icon(Icons.close, size: 18, color: AppColors.error),
              ),
            ],
          ),
          const SizedBox(height: 12),
          _f(entry.company, 'Company', required: true),
          _f(entry.position, 'Position / Role', required: true),
          Row(
            children: [
              Expanded(child: _dateF(entry.startDate, 'Start date')),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 8),
                child: Text('—',
                    style: TextStyle(color: AppColors.textSecondary, fontSize: 18)),
              ),
              Expanded(child: _dateF(entry.endDate, 'End date')),
            ],
          ),
          _f(entry.description, 'Responsibilities & achievements',
              maxLines: 3),
        ],
      ),
    );
  }

  Widget _f(TextEditingController c, String label,
      {bool required = false, int maxLines = 1}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        maxLines: maxLines,
        decoration: InputDecoration(
          labelText: required ? '$label *' : label,
          filled: true,
          fillColor: AppColors.surface,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.border)),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.border)),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.primary, width: 1.5)),
          isDense: true,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        validator: required
            ? (v) => (v == null || v.trim().isEmpty) ? 'Required' : null
            : null,
      ),
    );
  }

  Widget _dateF(TextEditingController c, String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        readOnly: true,
        decoration: InputDecoration(
          labelText: label,
          filled: true,
          fillColor: AppColors.surface,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.border)),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.border)),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: BorderSide(color: AppColors.primary, width: 1.5)),
          isDense: true,
          suffixIcon:
              Icon(Icons.calendar_month_outlined, size: 16, color: AppColors.textSecondary),
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        onTap: () => onPickDate(c),
      ),
    );
  }
}
