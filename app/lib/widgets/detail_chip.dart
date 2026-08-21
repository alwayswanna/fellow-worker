import 'package:flutter/material.dart';

import '../theme/app_theme.dart';

/// Small pill showing an icon + label, used on vacancy/company detail sheets.
class DetailChip extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color? color;

  const DetailChip(this.icon, this.label, {super.key, this.color});

  @override
  Widget build(BuildContext context) {
    final c = color ?? AppColors.textSecondary;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: c.withAlpha(20),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 15, color: c),
          const SizedBox(width: 5),
          Text(label, style: TextStyle(fontSize: 13, color: c)),
        ],
      ),
    );
  }
}

/// Icon + text row used in the contact-info block of a vacancy detail sheet.
class ContactRow extends StatelessWidget {
  final IconData icon;
  final String text;
  const ContactRow(this.icon, this.text, {super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 6),
      child: Row(children: [
        Icon(icon, size: 16, color: AppColors.primary),
        const SizedBox(width: 8),
        Expanded(child: Text(text,
            style: TextStyle(fontSize: 14, color: AppColors.text))),
      ]),
    );
  }
}
