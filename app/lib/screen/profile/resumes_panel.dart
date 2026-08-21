import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../theme/app_theme.dart';
import '../../widgets/widgets.dart';

/// Full "My resumes" list — used as the default role panel and inside the
/// APPLICANT tab bar.
class ResumesPanel extends StatelessWidget {
  final List<Resume> resumes;
  final bool isLoading;
  final VoidCallback onAdd;
  final void Function(Resume) onTap;
  final void Function(Resume) onEdit;

  const ResumesPanel({
    super.key,
    required this.resumes,
    required this.isLoading,
    required this.onAdd,
    required this.onTap,
    required this.onEdit,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.fromLTRB(24, 24, 24, 0),
          child: Text(
            'My resumes',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
        ),
        const SizedBox(height: 16),
        if (isLoading)
          const Padding(
            padding: EdgeInsets.all(32),
            child: Center(child: CircularProgressIndicator()),
          )
        else if (resumes.isEmpty)
          EmptyState(
            icon: Icons.description_outlined,
            title: 'No resumes yet',
            subtitle: 'Create your first resume to get started',
            actionLabel: 'Create resume',
            onAction: onAdd,
          )
        else
          ListView.separated(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
            itemCount: resumes.length,
            separatorBuilder: (_, __) => const SizedBox(height: 10),
            itemBuilder: (_, i) => ResumeCard(
              resume: resumes[i],
              onTap: () => onTap(resumes[i]),
              onEdit: () => onEdit(resumes[i]),
            ),
          ),
      ],
    );
  }
}

class ResumeCard extends StatelessWidget {
  final Resume resume;
  final VoidCallback onTap;
  final VoidCallback onEdit;

  const ResumeCard({
    super.key,
    required this.resume,
    required this.onTap,
    required this.onEdit,
  });

  @override
  Widget build(BuildContext context) {
    final title = resume.desiredPosition?.isNotEmpty == true
        ? resume.desiredPosition!
        : 'Resume';

    return Material(
      color: AppColors.surface,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              InitialsAvatar(
                name: resume.firstName,
                radius: 22,
                borderRadius: 8,
                color: AppColors.avatarColor(resume.fullName).withAlpha(38),
                textColor: AppColors.avatarColor(resume.fullName),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                          fontWeight: FontWeight.w600, fontSize: 15),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      resume.fullName,
                      style: TextStyle(
                          fontSize: 13, color: AppColors.textSecondary),
                    ),
                    if (resume.skills.isNotEmpty) ...[
                      const SizedBox(height: 6),
                      Wrap(
                        spacing: 4,
                        runSpacing: 4,
                        children: resume.skills.take(3).map((s) => Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8, vertical: 2),
                          decoration: BoxDecoration(
                            color: AppColors.tagBg,
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(s,
                              style: TextStyle(
                                  fontSize: 11,
                                  color: AppColors.tagText)),
                        )).toList(),
                      ),
                    ],
                  ],
                ),
              ),
              IconButton(
                icon: Icon(Icons.edit_outlined,
                    size: 18, color: AppColors.textSecondary),
                tooltip: 'Edit',
                onPressed: onEdit,
              ),
              Icon(Icons.chevron_right,
                  size: 20, color: AppColors.border),
            ],
          ),
        ),
      ),
    );
  }
}
