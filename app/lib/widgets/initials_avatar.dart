import 'package:flutter/material.dart';

/// A circular avatar showing the first letter of [name] on a colored background.
/// Optionally displays a [backgroundImage] instead.
class InitialsAvatar extends StatelessWidget {
  final String name;
  final double radius;
  final Color? color;
  final ImageProvider? backgroundImage;

  const InitialsAvatar({
    super.key,
    required this.name,
    this.radius = 20,
    this.color,
    this.backgroundImage,
  });

  @override
  Widget build(BuildContext context) {
    final initial = name.isNotEmpty ? name[0].toUpperCase() : '?';
    final bg = color ?? Colors.indigo;
    return CircleAvatar(
      radius: radius,
      backgroundColor: bg,
      backgroundImage: backgroundImage,
      child: backgroundImage == null
          ? Text(
              initial,
              style: TextStyle(
                color: Colors.white,
                fontSize: radius * 0.85,
                fontWeight: FontWeight.w600,
              ),
            )
          : null,
    );
  }
}
