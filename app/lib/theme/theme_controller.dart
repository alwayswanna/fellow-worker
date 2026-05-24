import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// App-wide light/dark switch. [AppColors] reads [isDark] directly so every
/// existing `AppColors.x` call site repaints on toggle without having to be
/// rewritten to thread `BuildContext`/`Theme.of(context)` through.
class ThemeController {
  ThemeController._();

  static final ThemeController instance = ThemeController._();

  static const _prefsKey = 'theme_mode';

  final ValueNotifier<ThemeMode> mode = ValueNotifier(ThemeMode.dark);

  bool get isDark => mode.value == ThemeMode.dark;

  Future<void> load() async {
    final prefs = await SharedPreferences.getInstance();
    final stored = prefs.getString(_prefsKey);
    if (stored == 'light') mode.value = ThemeMode.light;
    if (stored == 'dark') mode.value = ThemeMode.dark;
  }

  Future<void> toggle() async {
    mode.value = isDark ? ThemeMode.light : ThemeMode.dark;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_prefsKey, isDark ? 'dark' : 'light');
  }
}
