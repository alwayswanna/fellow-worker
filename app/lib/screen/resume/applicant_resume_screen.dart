import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../theme/app_theme.dart';
import '../../widgets/widgets.dart';
import 'resume_detail_screen.dart';

/// Read-only view of another account's resume(s) — used by recruiters
/// looking up an applicant. No edit/delete actions, unlike
/// [ResumeDetailScreen] which is for the resume's own owner.
class ApplicantResumeScreen extends StatelessWidget {
  final String applicantName;
  final List<Resume> resumes;

  const ApplicantResumeScreen({
    super.key,
    required this.applicantName,
    required this.resumes,
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
        title: Text(
          "$applicantName's resume",
          style: const TextStyle(fontWeight: FontWeight.bold),
          overflow: TextOverflow.ellipsis,
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
      ),
      body: resumes.isEmpty
          ? const EmptyState(
              icon: Icons.description_outlined,
              title: 'No resume yet',
              subtitle: "This applicant hasn't created a resume.",
            )
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  for (final r in resumes) ...[
                    ResumeDetailBody(resume: r),
                    if (r != resumes.last) ...[
                      const SizedBox(height: 20),
                      Divider(color: AppColors.border),
                      const SizedBox(height: 8),
                    ],
                  ],
                ],
              ),
            ),
    );
  }
}
