import 'package:flutter/material.dart';

import '../model/application.dart';
import '../theme/app_theme.dart';

/// A colored pill badge showing an [ApplicationStatus].
class StatusBadge extends StatelessWidget {
  final ApplicationStatus status;

  const StatusBadge(this.status, {super.key});

  static Color colorFor(ApplicationStatus s) => switch (s) {
        ApplicationStatus.ACCEPTED => AppColors.success,
        ApplicationStatus.REJECTED => AppColors.error,
        ApplicationStatus.WITHDRAWN => AppColors.textSecondary,
        ApplicationStatus.REVIEWED => AppColors.primary,
        ApplicationStatus.PENDING => AppColors.warning,
      };

  @override
  Widget build(BuildContext context) {
    final color = colorFor(status);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 3),
      decoration: BoxDecoration(
        color: color.withAlpha(25),
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: color.withAlpha(80)),
      ),
      child: Text(
        status.displayName,
        style: TextStyle(
            fontSize: 12, color: color, fontWeight: FontWeight.w600),
      ),
    );
  }
}
