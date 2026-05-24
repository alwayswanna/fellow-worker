import 'package:flutter/material.dart';

import '../../../theme/app_theme.dart';

class SkillsSection extends StatefulWidget {
  final List<String> skills;
  final ValueChanged<String> onAdd;
  final ValueChanged<String> onRemove;

  const SkillsSection({
    super.key,
    required this.skills,
    required this.onAdd,
    required this.onRemove,
  });

  @override
  State<SkillsSection> createState() => _SkillsSectionState();
}

class _SkillsSectionState extends State<SkillsSection> {
  final _skillInput = TextEditingController();

  @override
  void dispose() {
    _skillInput.dispose();
    super.dispose();
  }

  void _submit() {
    final s = _skillInput.text.trim();
    if (s.isEmpty || widget.skills.contains(s)) return;
    widget.onAdd(s);
    _skillInput.clear();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: TextFormField(
                controller: _skillInput,
                decoration: InputDecoration(
                  hintText: 'e.g. Java, Spring Boot, PostgreSQL',
                  hintStyle: TextStyle(color: AppColors.textSecondary),
                  filled: true,
                  fillColor: AppColors.bg,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: BorderSide(color: AppColors.border),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: BorderSide(color: AppColors.border),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide:
                        BorderSide(color: AppColors.primary, width: 1.5),
                  ),
                  isDense: true,
                  contentPadding:
                      const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
                ),
                onFieldSubmitted: (_) => _submit(),
              ),
            ),
            const SizedBox(width: 8),
            SizedBox(
              height: 48,
              child: FilledButton(
                onPressed: _submit,
                style: FilledButton.styleFrom(
                  backgroundColor: AppColors.accent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                ),
                child: const Text('Add'),
              ),
            ),
          ],
        ),
        if (widget.skills.isNotEmpty) ...[
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: widget.skills
                .map((s) => Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(
                        color: AppColors.tagBg,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(s,
                              style: TextStyle(
                                  fontSize: 13,
                                  color: AppColors.tagText,
                                  fontWeight: FontWeight.w500)),
                          const SizedBox(width: 6),
                          GestureDetector(
                            onTap: () => widget.onRemove(s),
                            child: Icon(Icons.close,
                                size: 14, color: AppColors.primary),
                          ),
                        ],
                      ),
                    ))
                .toList(),
          ),
        ],
      ],
    );
  }
}
