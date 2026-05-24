import 'package:flutter/material.dart';

import '../theme/app_theme.dart';

/// Shows the first letter of [name] on a colored background — circular by
/// default, or a rounded square when [borderRadius] is given. Optionally
/// displays a [backgroundImage] instead of the initial.
class InitialsAvatar extends StatelessWidget {
  final String name;
  final double radius;
  final Color? color;
  final Color? textColor;
  final ImageProvider? backgroundImage;
  final double? borderRadius;

  const InitialsAvatar({
    super.key,
    required this.name,
    this.radius = 20,
    this.color,
    this.textColor,
    this.backgroundImage,
    this.borderRadius,
  });

  @override
  Widget build(BuildContext context) {
    final initial = name.isNotEmpty ? name[0].toUpperCase() : '?';
    final bg = color ?? AppColors.primary;
    final fg = textColor ?? Colors.white;
    final label = Text(
      initial,
      style: TextStyle(color: fg, fontSize: radius * 0.85, fontWeight: FontWeight.w600),
    );

    if (borderRadius == null) {
      return CircleAvatar(
        radius: radius,
        backgroundColor: bg,
        backgroundImage: backgroundImage,
        child: backgroundImage == null ? label : null,
      );
    }

    final size = radius * 2;
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(borderRadius!)),
      alignment: Alignment.center,
      child: backgroundImage != null
          ? ClipRRect(
              borderRadius: BorderRadius.circular(borderRadius!),
              child: Image(image: backgroundImage!, width: size, height: size, fit: BoxFit.cover),
            )
          : label,
    );
  }
}
