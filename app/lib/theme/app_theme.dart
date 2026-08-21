import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

/// Design tokens for the "Fellow Worker" visual language: warm ivory
/// background, deep indigo as the primary color, coral as the action
/// accent, Fraunces for display type and Public Sans for everything else.
class AppColors {
  AppColors._();

  static const bg = Color(0xFFF7F5F1);
  static const surface = Color(0xFFFFFFFF);
  static const border = Color(0xFFE3DFD6);
  static const text = Color(0xFF1B1E29);
  static const textSecondary = Color(0xFF6B6F7B);
  static const primary = Color(0xFF243B7A);
  static const primaryDark = Color(0xFF182B5C);
  static const accent = Color(0xFFFF6A3D);
  static const accentDark = Color(0xFFE5572B);
  static const success = Color(0xFF2E7D5B);
  static const successBg = Color(0xFFE6F4EC);
  static const tagBg = Color(0xFFEEF1F8);
  static const tagText = Color(0xFF33396B);
  static const error = Color(0xFFDC2626);
  static const errorBg = Color(0xFFFDECEA);
  static const warning = Color(0xFFB4680A);
}

class AppTheme {
  AppTheme._();

  static ThemeData get theme {
    final colorScheme = ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      brightness: Brightness.light,
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

    final bodyFont = GoogleFonts.publicSansTextTheme();
    final displayFont = GoogleFonts.frauncesTextTheme();

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
          borderRadius: BorderRadius.circular(12),
          side: const BorderSide(color: AppColors.border),
        ),
      ),
      dividerTheme: const DividerThemeData(
        color: AppColors.border,
        thickness: 1,
        space: 1,
      ),
      chipTheme: ChipThemeData(
        backgroundColor: AppColors.tagBg,
        labelStyle: const TextStyle(
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
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.primary, width: 1.5),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.error),
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.accent,
          foregroundColor: Colors.white,
          disabledBackgroundColor: AppColors.accent.withAlpha(120),
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: AppColors.accent,
          foregroundColor: Colors.white,
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: const BorderSide(color: AppColors.border),
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          textStyle: const TextStyle(fontWeight: FontWeight.w600),
        ),
      ),
      tabBarTheme: const TabBarTheme(
        labelColor: AppColors.primary,
        unselectedLabelColor: AppColors.textSecondary,
        indicatorColor: AppColors.primary,
      ),
      dialogTheme: DialogTheme(
        backgroundColor: AppColors.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      iconTheme: const IconThemeData(color: AppColors.textSecondary),
    );
  }
}
