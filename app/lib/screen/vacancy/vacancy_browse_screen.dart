import 'package:flutter/material.dart';

import '../../service/vacancy_api_service.dart';
import '../../widgets/widgets.dart';
import '../../theme/app_theme.dart';

/// Public entry point: lets anyone browse open vacancies without signing in.
/// Applying still requires an account, prompted inline via [JobSearchPanel].
class VacancyBrowseScreen extends StatelessWidget {
  final VacancyApiService vacancyApiService;

  const VacancyBrowseScreen({super.key, required this.vacancyApiService});

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
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
        actions: [
          TextButton.icon(
            onPressed: () => Navigator.of(context).pushNamed('/login'),
            icon: const Icon(Icons.login, size: 18),
            label: const Text('Sign in'),
            style: TextButton.styleFrom(foregroundColor: AppColors.primary),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: JobSearchPanel(
        vacancyApiService: vacancyApiService,
        onSignInToApply: () => Navigator.of(context).pushNamed('/login'),
      ),
    );
  }
}