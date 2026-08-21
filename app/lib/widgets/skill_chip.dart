import 'package:flutter/material.dart';

import '../theme/app_theme.dart';

/// A small pill chip used to display a skill tag.
class SkillChip extends StatelessWidget {
  final String label;

  const SkillChip(this.label, {super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: AppColors.tagBg,
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        label,
        style: const TextStyle(fontSize: 12, color: AppColors.tagText, fontWeight: FontWeight.w500),
      ),
    );
  }
}
