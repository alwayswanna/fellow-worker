import 'package:flutter/material.dart';

/// A centered empty-state view with an icon, title, optional subtitle and action.
///
/// Supply [actionLabel] + [onAction] for the default FilledButton,
/// or pass [action] directly for full control over the button type.
class EmptyState extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final String? actionLabel;
  final VoidCallback? onAction;
  /// Custom action widget that overrides [actionLabel]/[onAction].
  final Widget? action;

  const EmptyState({
    super.key,
    required this.icon,
    required this.title,
    this.subtitle,
    this.actionLabel,
    this.onAction,
    this.action,
  });

  @override
  Widget build(BuildContext context) {
    Widget? actionWidget = action;
    if (actionWidget == null && actionLabel != null && onAction != null) {
      actionWidget = FilledButton.icon(
        onPressed: onAction,
        icon: const Icon(Icons.add, size: 18),
        label: Text(actionLabel!),
        style: FilledButton.styleFrom(
          backgroundColor: Colors.indigo,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        ),
      );
    }

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 52, color: Colors.indigo.shade200),
            const SizedBox(height: 16),
            Text(
              title,
              style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Color(0xFF333333)),
              textAlign: TextAlign.center,
            ),
            if (subtitle != null) ...[
              const SizedBox(height: 6),
              Text(
                subtitle!,
                style: const TextStyle(fontSize: 13, color: Color(0xFF888888)),
                textAlign: TextAlign.center,
              ),
            ],
            if (actionWidget != null) ...[
              const SizedBox(height: 20),
              actionWidget,
            ],
          ],
        ),
      ),
    );
  }
}
