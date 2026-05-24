import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../service/resume_api_service.dart';
import '../../util/dialogs.dart';
import '../../theme/app_theme.dart';

class ResumeDetailScreen extends StatelessWidget {
  final Resume resume;
  final ResumeApiService resumeApiService;
  final VoidCallback onEdit;

  const ResumeDetailScreen({
    super.key,
    required this.resume,
    required this.resumeApiService,
    required this.onEdit,
  });

  Future<void> _confirmDelete(BuildContext context) async {
    final confirmed = await confirmDialog(
      context,
      title: 'Delete resume?',
      message: 'This action cannot be undone.',
    );
    if (!confirmed || !context.mounted) return;
    final ok = await resumeApiService.deleteResume(resume.id);
    if (context.mounted) {
      Navigator.of(context).pop(ok);
    }
  }

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
          resume.desiredPosition ?? resume.fullName,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          overflow: TextOverflow.ellipsis,
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_outlined),
            tooltip: 'Edit',
            onPressed: onEdit,
          ),
          IconButton(
            icon: Icon(Icons.delete_outline, color: AppColors.error),
            tooltip: 'Delete',
            onPressed: () => _confirmDelete(context),
          ),
          const SizedBox(width: 4),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: ResumeDetailBody(resume: resume),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        child: FilledButton.icon(
          onPressed: onEdit,
          icon: const Icon(Icons.edit_outlined),
          label: const Text('Edit resume',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
          style: FilledButton.styleFrom(
            backgroundColor: AppColors.accent,
            minimumSize: const Size.fromHeight(52),
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          ),
        ),
      ),
    );
  }
}

// ── Reusable body (shared with read-only recruiter view) ───────────────────

/// The scrollable content of a resume — header, summary, skills, experience,
/// education, links — without the owner-only AppBar actions or edit button,
/// so it can be reused for a recruiter's read-only view of someone else's
/// resume.
class ResumeDetailBody extends StatelessWidget {
  final Resume resume;

  const ResumeDetailBody({super.key, required this.resume});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        _HeaderCard(resume: resume),
        if (resume.summary?.isNotEmpty == true) ...[
          const SizedBox(height: 12),
          _SummaryCard(summary: resume.summary!),
        ],
        if (resume.skills.isNotEmpty) ...[
          const SizedBox(height: 12),
          _SkillsCard(skills: resume.skills),
        ],
        if (resume.experience.isNotEmpty) ...[
          const SizedBox(height: 12),
          _ExperienceCard(experience: resume.experience),
        ],
        if (resume.education.isNotEmpty) ...[
          const SizedBox(height: 12),
          _EducationCard(education: resume.education),
        ],
        if (resume.links.isNotEmpty) ...[
          const SizedBox(height: 12),
          _LinksCard(links: resume.links),
        ],
      ],
    );
  }
}

// ── Section card base ──────────────────────────────────────────────────────

class _SectionCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final Widget child;

  const _SectionCard({
    required this.icon,
    required this.title,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
            child: Row(
              children: [
                Icon(icon, size: 18, color: AppColors.primary),
                const SizedBox(width: 8),
                Text(
                  title,
                  style: TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.bold,
                    color: AppColors.text,
                  ),
                ),
              ],
            ),
          ),
          Divider(height: 1, color: AppColors.bg),
          Padding(
            padding: const EdgeInsets.all(16),
            child: child,
          ),
        ],
      ),
    );
  }
}

// ── Header card ────────────────────────────────────────────────────────────

class _HeaderCard extends StatelessWidget {
  final Resume resume;
  const _HeaderCard({required this.resume});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                width: 64,
                height: 64,
                decoration: BoxDecoration(
                  color: AppColors.tagBg,
                  borderRadius: BorderRadius.circular(12),
                  // resume-app's photoUrl is already a full MinIO URL, unlike user-app's
                  // relative photoUrl — must not be prefixed with AppConfig.photoBaseUrl.
                  image: resume.photoUrl != null
                      ? DecorationImage(
                          image: NetworkImage(resume.photoUrl!),
                          fit: BoxFit.cover,
                        )
                      : null,
                ),
                alignment: Alignment.center,
                child: resume.photoUrl != null
                    ? null
                    : Text(
                        resume.firstName.isNotEmpty
                            ? resume.firstName[0].toUpperCase()
                            : '?',
                        style: TextStyle(
                            fontSize: 28,
                            fontWeight: FontWeight.bold,
                            color: AppColors.tagText),
                      ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      resume.fullName,
                      style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: AppColors.text),
                    ),
                    if (resume.desiredPosition?.isNotEmpty == true) ...[
                      const SizedBox(height: 4),
                      Text(
                        resume.desiredPosition!,
                        style: TextStyle(
                            fontSize: 15,
                            color: AppColors.primary,
                            fontWeight: FontWeight.w500),
                      ),
                    ],
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 3),
                      decoration: BoxDecoration(
                        color: AppColors.successBg,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        'Active',
                        style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: AppColors.success),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Divider(height: 1, color: AppColors.bg),
          const SizedBox(height: 16),
          _contactRow(Icons.email_outlined, resume.email),
          if (resume.phone?.isNotEmpty == true) ...[
            const SizedBox(height: 8),
            _contactRow(Icons.phone_outlined, resume.phone!),
          ],
        ],
      ),
    );
  }

  Widget _contactRow(IconData icon, String value) {
    return Row(
      children: [
        Icon(icon, size: 16, color: AppColors.textSecondary),
        const SizedBox(width: 10),
        Text(value,
            style: TextStyle(fontSize: 14, color: AppColors.text)),
      ],
    );
  }
}

// ── Summary ────────────────────────────────────────────────────────────────

class _SummaryCard extends StatelessWidget {
  final String summary;
  const _SummaryCard({required this.summary});

  @override
  Widget build(BuildContext context) {
    return _SectionCard(
      icon: Icons.notes_outlined,
      title: 'About me',
      child: Text(
        summary,
        style: TextStyle(
            fontSize: 14, color: AppColors.text, height: 1.6),
      ),
    );
  }
}

// ── Skills ─────────────────────────────────────────────────────────────────

class _SkillsCard extends StatelessWidget {
  final List<String> skills;
  const _SkillsCard({required this.skills});

  @override
  Widget build(BuildContext context) {
    return _SectionCard(
      icon: Icons.star_outline,
      title: 'Skills',
      child: Wrap(
        spacing: 8,
        runSpacing: 8,
        children: skills
            .map((s) => Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: AppColors.tagBg,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    s,
                    style: TextStyle(
                        fontSize: 13,
                        color: AppColors.tagText,
                        fontWeight: FontWeight.w500),
                  ),
                ))
            .toList(),
      ),
    );
  }
}

// ── Work Experience ────────────────────────────────────────────────────────

class _ExperienceCard extends StatelessWidget {
  final List<WorkExperience> experience;
  const _ExperienceCard({required this.experience});

  @override
  Widget build(BuildContext context) {
    return _SectionCard(
      icon: Icons.work_outline,
      title: 'Work Experience',
      child: Column(
        children: experience.asMap().entries.map((entry) {
          final i = entry.key;
          final e = entry.value;
          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (i > 0) ...[
                Divider(height: 24, color: AppColors.bg),
              ],
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: AppColors.bg,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Icon(Icons.business_outlined,
                        size: 18, color: AppColors.textSecondary),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(e.company,
                            style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 15,
                                color: AppColors.text)),
                        const SizedBox(height: 2),
                        Text(e.position,
                            style: TextStyle(
                                fontSize: 14, color: AppColors.primary,
                                fontWeight: FontWeight.w500)),
                        if (e.startDate != null || e.endDate != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            _dateRange(e.startDate, e.endDate),
                            style: TextStyle(
                                fontSize: 13, color: AppColors.textSecondary),
                          ),
                        ],
                        if (e.description?.isNotEmpty == true) ...[
                          const SizedBox(height: 8),
                          Text(e.description!,
                              style: TextStyle(
                                  fontSize: 14,
                                  color: AppColors.text,
                                  height: 1.5)),
                        ],
                      ],
                    ),
                  ),
                ],
              ),
            ],
          );
        }).toList(),
      ),
    );
  }

  String _dateRange(String? start, String? end) {
    final s = start ?? '';
    final e = end?.isNotEmpty == true ? end! : 'present';
    return '$s — $e';
  }
}

// ── Education ──────────────────────────────────────────────────────────────

class _EducationCard extends StatelessWidget {
  final List<Education> education;
  const _EducationCard({required this.education});

  @override
  Widget build(BuildContext context) {
    return _SectionCard(
      icon: Icons.school_outlined,
      title: 'Education',
      child: Column(
        children: education.asMap().entries.map((entry) {
          final i = entry.key;
          final e = entry.value;
          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (i > 0) ...[
                Divider(height: 24, color: AppColors.bg),
              ],
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: AppColors.bg,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Icon(Icons.account_balance_outlined,
                        size: 18, color: AppColors.textSecondary),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(e.institution,
                            style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 15,
                                color: AppColors.text)),
                        const SizedBox(height: 2),
                        Text('${e.degree} · ${e.fieldOfStudy}',
                            style: TextStyle(
                                fontSize: 14, color: AppColors.text)),
                        if (e.startDate != null || e.endDate != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            _dateRange(e.startDate, e.endDate),
                            style: TextStyle(
                                fontSize: 13, color: AppColors.textSecondary),
                          ),
                        ],
                      ],
                    ),
                  ),
                ],
              ),
            ],
          );
        }).toList(),
      ),
    );
  }

  String _dateRange(String? start, String? end) {
    final s = start ?? '';
    final e = end?.isNotEmpty == true ? end! : 'present';
    return '$s — $e';
  }
}

// ── Links ──────────────────────────────────────────────────────────────────

class _LinksCard extends StatelessWidget {
  final List<String> links;
  const _LinksCard({required this.links});

  @override
  Widget build(BuildContext context) {
    return _SectionCard(
      icon: Icons.link_outlined,
      title: 'Links',
      child: Column(
        children: links
            .map((link) => Padding(
                  padding: const EdgeInsets.only(bottom: 8),
                  child: Row(
                    children: [
                      Icon(Icons.open_in_new,
                          size: 14, color: AppColors.primary),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          link,
                          style: TextStyle(
                              fontSize: 14,
                              color: AppColors.primary,
                              decoration: TextDecoration.underline,
                              decorationColor: AppColors.primary),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                ))
            .toList(),
      ),
    );
  }
}
