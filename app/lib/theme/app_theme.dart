import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'theme_controller.dart';

/// Dark palette — doubles as the canonical values for the Spring-rendered
/// login page (`user-app/.../templates/login.html`), so the two surfaces
/// share one dark identity.
class _DarkPalette {
  _DarkPalette._();

  static const bg = Color(0xFF0F172A);
  static const surface = Color(0xFF1E293B);
  static const border = Color(0xFF334155);
  static const text = Color(0xFFF1F5F9);
  static const textSecondary = Color(0xFF94A3B8);
  static const primary = Color(0xFF818CF8);
  static const accent = Color(0xFFA78BFA);
  static const success = Color(0xFF22C55E);
  static const successBg = Color(0x2622C55E);
  static const tagBg = Color(0x296366F1);
  static const tagText = Color(0xFFA5B4FC);
  static const error = Color(0xFFEF4444);
  static const errorBg = Color(0x29EF4444);
  static const warning = Color(0xFFF59E0B);
  static const cardShadow = Color(0x66000000);
}

class _LightPalette {
  _LightPalette._();

  static const bg = Color(0xFFF5F6FA);
  static const surface = Color(0xFFFFFFFF);
  static const border = Color(0xFFE2E5EC);
  static const text = Color(0xFF0F172A);
  static const textSecondary = Color(0xFF64748B);
  static const primary = Color(0xFF6366F1);
  static const accent = Color(0xFF8B5CF6);
  static const success = Color(0xFF16A34A);
  static const successBg = Color(0xFFE7F7ED);
  static const tagBg = Color(0xFFEEF0FF);
  static const tagText = Color(0xFF4338CA);
  static const error = Color(0xFFDC2626);
  static const errorBg = Color(0xFFFDECEC);
  static const warning = Color(0xFFD97706);
  static const cardShadow = Color(0x14111827);
}

/// Design tokens for the "Fellow Worker" visual language: indigo/violet
/// accents on a slate dark theme (matching the OAuth login page) or a cool
/// neutral light theme, Sora for display type and Inter for everything else.
///
/// Fields resolve dynamically from [ThemeController] instead of being
/// `static const`, so every existing `AppColors.x` call site repaints on
/// theme toggle without needing `Theme.of(context)` threaded through.
class AppColors {
  AppColors._();

  static bool get _isDark => ThemeController.instance.isDark;

  static Color get bg => _isDark ? _DarkPalette.bg : _LightPalette.bg;
  static Color get surface => _isDark ? _DarkPalette.surface : _LightPalette.surface;
  static Color get border => _isDark ? _DarkPalette.border : _LightPalette.border;
  static Color get text => _isDark ? _DarkPalette.text : _LightPalette.text;
  static Color get textSecondary =>
      _isDark ? _DarkPalette.textSecondary : _LightPalette.textSecondary;
  static Color get primary => _isDark ? _DarkPalette.primary : _LightPalette.primary;
  static Color get accent => _isDark ? _DarkPalette.accent : _LightPalette.accent;
  static Color get success => _isDark ? _DarkPalette.success : _LightPalette.success;
  static Color get successBg => _isDark ? _DarkPalette.successBg : _LightPalette.successBg;
  static Color get tagBg => _isDark ? _DarkPalette.tagBg : _LightPalette.tagBg;
  static Color get tagText => _isDark ? _DarkPalette.tagText : _LightPalette.tagText;
  static Color get error => _isDark ? _DarkPalette.error : _LightPalette.error;
  static Color get errorBg => _isDark ? _DarkPalette.errorBg : _LightPalette.errorBg;
  static Color get warning => _isDark ? _DarkPalette.warning : _LightPalette.warning;
  static Color get cardShadow => _isDark ? _DarkPalette.cardShadow : _LightPalette.cardShadow;

  /// Gradient used for the highest-emphasis CTAs (sign in, apply) — mirrors
  /// the login page's button gradient.
  static LinearGradient get ctaGradient => LinearGradient(
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
        colors: _isDark
            ? const [Color(0xFF6366F1), Color(0xFF8B5CF6)]
            : const [Color(0xFF6366F1), Color(0xFF8B5CF6)],
      );

  static const List<Color> _avatarPaletteLight = [
    Color(0xFF6366F1),
    Color(0xFF8B5CF6),
    Color(0xFF0D9488),
    Color(0xFFD97706),
    Color(0xFFE11D48),
    Color(0xFF059669),
    Color(0xFF0284C7),
    Color(0xFFC026D3),
  ];

  static const List<Color> _avatarPaletteDark = [
    Color(0xFF818CF8),
    Color(0xFFA78BFA),
    Color(0xFF2DD4BF),
    Color(0xFFFBBF24),
    Color(0xFFFB7185),
    Color(0xFF34D399),
    Color(0xFF38BDF8),
    Color(0xFFE879F9),
  ];

  /// Deterministic color for an avatar initial, hashed from [seed] (an id or
  /// name) so the same entity always gets the same hue instead of the old
  /// flat gray box.
  static Color avatarColor(String seed) {
    if (seed.isEmpty) return textSecondary;
    final palette = _isDark ? _avatarPaletteDark : _avatarPaletteLight;
    final hash = seed.codeUnits.fold<int>(0, (acc, c) => acc + c);
    return palette[hash % palette.length];
  }
}

class AppTheme {
  AppTheme._();

  /// Single dynamic theme — brightness and every color token follow
  /// [ThemeController], so callers just rebuild the tree on toggle instead
  /// of juggling separate light/dark [ThemeData] instances.
  static ThemeData get theme {
    final isDark = ThemeController.instance.isDark;
    final brightness = isDark ? Brightness.dark : Brightness.light;

    final colorScheme = ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      brightness: brightness,
    ).copyWith(
      primary: AppColors.primary,
      onPrimary: Colors.white,
      secondary: AppColors.accent,
      onSecondary: Colors.white,
      surface: AppColors.surface,
      onSurface: AppColors.text,
      error: AppColors.error,
      onError: Colors.white,
    );

    final bodyFont = GoogleFonts.interTextTheme();
    final displayFont = GoogleFonts.soraTextTheme();

    final textTheme = bodyFont.copyWith(
      displayLarge: displayFont.displayLarge
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      displayMedium: displayFont.displayMedium
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      displaySmall: displayFont.displaySmall
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      headlineLarge: displayFont.headlineLarge
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      headlineMedium: displayFont.headlineMedium
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      headlineSmall: displayFont.headlineSmall
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w600),
      titleLarge: displayFont.titleLarge
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w700),
      titleMedium: bodyFont.titleMedium
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w600),
      titleSmall: bodyFont.titleSmall
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w600),
      bodyLarge: bodyFont.bodyLarge?.copyWith(color: AppColors.text),
      bodyMedium: bodyFont.bodyMedium?.copyWith(color: AppColors.text),
      bodySmall: bodyFont.bodySmall?.copyWith(color: AppColors.textSecondary),
      labelLarge: bodyFont.labelLarge
          ?.copyWith(color: AppColors.text, fontWeight: FontWeight.w600),
    );

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: AppColors.bg,
      textTheme: textTheme,
      fontFamily: bodyFont.bodyMedium?.fontFamily,
      appBarTheme: AppBarTheme(
        backgroundColor: AppColors.surface,
        foregroundColor: AppColors.text,
        elevation: 0,
        surfaceTintColor: AppColors.surface,
        titleTextStyle: displayFont.titleLarge?.copyWith(
          color: AppColors.text,
          fontWeight: FontWeight.w700,
          fontSize: 20,
        ),
      ),
      cardTheme: CardTheme(
        color: AppColors.surface,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: BorderSide(color: AppColors.border),
        ),
      ),
      dividerTheme: DividerThemeData(
        color: AppColors.border,
        thickness: 1,
        space: 1,
      ),
      chipTheme: ChipThemeData(
        backgroundColor: AppColors.tagBg,
        labelStyle: TextStyle(
          color: AppColors.tagText,
          fontSize: 13,
          fontWeight: FontWeight.w500,
        ),
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(999)),
        side: BorderSide.none,
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: AppColors.surface,
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.border),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.border),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.primary, width: 1.5),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.error),
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.accent,
          foregroundColor: Colors.white,
          disabledBackgroundColor: AppColors.accent.withAlpha(120),
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: AppColors.accent,
          foregroundColor: Colors.white,
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: BorderSide(color: AppColors.border),
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          textStyle: const TextStyle(fontWeight: FontWeight.w600),
        ),
      ),
      tabBarTheme: TabBarTheme(
        labelColor: AppColors.primary,
        unselectedLabelColor: AppColors.textSecondary,
        indicatorColor: AppColors.primary,
      ),
      dialogTheme: DialogTheme(
        backgroundColor: AppColors.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      ),
      iconTheme: IconThemeData(color: AppColors.textSecondary),
    );
  }
}
