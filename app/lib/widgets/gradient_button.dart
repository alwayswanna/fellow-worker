import 'package:flutter/material.dart';

import '../theme/app_theme.dart';

/// Highest-emphasis action (apply, sign in) — the indigo→violet gradient
/// mirrors the OAuth login page's button instead of a flat fill.
class GradientButton extends StatelessWidget {
  final VoidCallback? onPressed;
  final Widget icon;
  final Widget label;

  const GradientButton({
    super.key,
    required this.onPressed,
    required this.icon,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    final disabled = onPressed == null;
    return SizedBox(
      width: double.infinity,
      height: 44,
      child: DecoratedBox(
        decoration: BoxDecoration(
          gradient: disabled ? null : AppColors.ctaGradient,
          color: disabled ? AppColors.border : null,
          borderRadius: BorderRadius.circular(10),
        ),
        child: FilledButton.icon(
          onPressed: onPressed,
          icon: icon,
          label: label,
          style: FilledButton.styleFrom(
            backgroundColor: Colors.transparent,
            disabledBackgroundColor: Colors.transparent,
            foregroundColor: Colors.white,
            shadowColor: Colors.transparent,
            elevation: 0,
          ),
        ),
      ),
    );
  }
}
