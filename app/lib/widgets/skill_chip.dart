import 'package:flutter/material.dart';

/// A small indigo chip used to display a skill tag.
class SkillChip extends StatelessWidget {
  final String label;

  const SkillChip(this.label, {super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: const Color(0xFFEEF2FF),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        label,
        style: TextStyle(fontSize: 12, color: Colors.indigo.shade700),
      ),
    );
  }
}
