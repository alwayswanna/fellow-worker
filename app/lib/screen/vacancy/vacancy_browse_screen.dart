import 'package:flutter/material.dart';

import '../../service/auth_service.dart';
import '../../service/vacancy_api_service.dart';
import '../../widgets/widgets.dart';
import '../../theme/app_theme.dart';
import '../../theme/theme_controller.dart';

/// Public entry point: lets anyone browse open vacancies without signing in.
/// Applying still requires an account, prompted inline via [JobSearchPanel].
class VacancyBrowseScreen extends StatelessWidget {
  final VacancyApiService vacancyApiService;
  final AuthService authService;

  const VacancyBrowseScreen({
    super.key,
    required this.vacancyApiService,
    required this.authService,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.bg,
      appBar: AppBar(
        backgroundColor: AppColors.surface,
        foregroundColor: AppColors.text,
        elevation: 0,
        surfaceTintColor: AppColors.surface,
        title: const Text(
          'Fellow Worker',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
        ),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
        actions: [
          const ThemeToggleButton(),
          TextButton(
            onPressed: () => Navigator.of(context).pushNamed('/register'),
            style: TextButton.styleFrom(foregroundColor: AppColors.textSecondary),
            child: const Text('Register'),
          ),
          TextButton.icon(
            onPressed: authService.login,
            icon: const Icon(Icons.login, size: 18),
            label: const Text('Sign in'),
            style: TextButton.styleFrom(foregroundColor: AppColors.primary),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: JobSearchPanel(
        vacancyApiService: vacancyApiService,
        onSignInToApply: authService.login,
      ),
    );
  }
}

/// Sun/moon switch shared by every top-level app bar; toggles
/// [ThemeController] and persists the choice.
class ThemeToggleButton extends StatelessWidget {
  const ThemeToggleButton({super.key});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<ThemeMode>(
      valueListenable: ThemeController.instance.mode,
      builder: (context, mode, _) => IconButton(
        onPressed: ThemeController.instance.toggle,
        tooltip: mode == ThemeMode.dark ? 'Switch to light mode' : 'Switch to dark mode',
        icon: Icon(mode == ThemeMode.dark ? Icons.light_mode_outlined : Icons.dark_mode_outlined),
      ),
    );
  }
}
