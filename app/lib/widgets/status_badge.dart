import 'package:flutter/material.dart';

import '../model/application.dart';

/// A colored pill badge showing an [ApplicationStatus].
class StatusBadge extends StatelessWidget {
  final ApplicationStatus status;

  const StatusBadge(this.status, {super.key});

  static Color colorFor(ApplicationStatus s) => switch (s) {
        ApplicationStatus.ACCEPTED => Colors.green,
        ApplicationStatus.REJECTED => Colors.red,
        ApplicationStatus.WITHDRAWN => Colors.grey,
        ApplicationStatus.REVIEWED => Colors.blue,
        ApplicationStatus.PENDING => Colors.orange,
      };

  @override
  Widget build(BuildContext context) {
    final color = colorFor(status);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 3),
      decoration: BoxDecoration(
        color: color.withAlpha(25),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withAlpha(80)),
      ),
      child: Text(
        status.displayName,
        style: TextStyle(
            fontSize: 12, color: color, fontWeight: FontWeight.w500),
      ),
    );
  }
}
