import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../service/resume_api_service.dart';

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
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        title: const Text('Delete resume?'),
        content: const Text('This action cannot be undone.'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          FilledButton(
            style: FilledButton.styleFrom(backgroundColor: Colors.red),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirmed != true || !context.mounted) return;
    final ok = await resumeApiService.deleteResume(resume.id);
    if (context.mounted) {
      Navigator.of(context).pop(ok);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF232F3E),
        elevation: 0,
        surfaceTintColor: Colors.white,
        title: Text(
          resume.desiredPosition ?? resume.fullName,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          overflow: TextOverflow.ellipsis,
        ),
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: Color(0xFFE0E0E0)),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_outlined),
            tooltip: 'Edit',
            onPressed: onEdit,
          ),
          IconButton(
            icon: const Icon(Icons.delete_outline, color: Colors.red),
            tooltip: 'Delete',
            onPressed: () => _confirmDelete(context),
          ),
          const SizedBox(width: 4),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
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
            const SizedBox(height: 24),
          ],
        ),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        child: FilledButton.icon(
          onPressed: onEdit,
          icon: const Icon(Icons.edit_outlined),
          label: const Text('Edit resume',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
          style: FilledButton.styleFrom(
            backgroundColor: Colors.indigo,
            minimumSize: const Size.fromHeight(52),
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          ),
        ),
      ),
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
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
            child: Row(
              children: [
                Icon(icon, size: 18, color: Colors.indigo),
                const SizedBox(width: 8),
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF232F3E),
                  ),
                ),
              ],
            ),
          ),
          const Divider(height: 1, color: Color(0xFFF0F0F0)),
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
        color: Colors.white,
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
                  color: Colors.indigo.shade50,
                  borderRadius: BorderRadius.circular(12),
                ),
                alignment: Alignment.center,
                child: Text(
                  resume.firstName.isNotEmpty
                      ? resume.firstName[0].toUpperCase()
                      : '?',
                  style: TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: Colors.indigo.shade700),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      resume.fullName,
                      style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF232F3E)),
                    ),
                    if (resume.desiredPosition?.isNotEmpty == true) ...[
                      const SizedBox(height: 4),
                      Text(
                        resume.desiredPosition!,
                        style: const TextStyle(
                            fontSize: 15,
                            color: Colors.indigo,
                            fontWeight: FontWeight.w500),
                      ),
                    ],
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 3),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE8F5E9),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const Text(
                        'Active',
                        style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: Color(0xFF2E7D32)),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          const Divider(height: 1, color: Color(0xFFF0F0F0)),
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
        Icon(icon, size: 16, color: const Color(0xFF999999)),
        const SizedBox(width: 10),
        Text(value,
            style: const TextStyle(fontSize: 14, color: Color(0xFF444444))),
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
        style: const TextStyle(
            fontSize: 14, color: Color(0xFF444444), height: 1.6),
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
                    color: const Color(0xFFEEF2FF),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    s,
                    style: TextStyle(
                        fontSize: 13,
                        color: Colors.indigo.shade700,
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
                const Divider(height: 24, color: Color(0xFFF0F0F0)),
              ],
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: const Color(0xFFF5F5F5),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: const Icon(Icons.business_outlined,
                        size: 18, color: Color(0xFF999999)),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(e.company,
                            style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 15,
                                color: Color(0xFF232F3E))),
                        const SizedBox(height: 2),
                        Text(e.position,
                            style: const TextStyle(
                                fontSize: 14, color: Colors.indigo,
                                fontWeight: FontWeight.w500)),
                        if (e.startDate != null || e.endDate != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            _dateRange(e.startDate, e.endDate),
                            style: const TextStyle(
                                fontSize: 13, color: Color(0xFF999999)),
                          ),
                        ],
                        if (e.description?.isNotEmpty == true) ...[
                          const SizedBox(height: 8),
                          Text(e.description!,
                              style: const TextStyle(
                                  fontSize: 14,
                                  color: Color(0xFF444444),
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
                const Divider(height: 24, color: Color(0xFFF0F0F0)),
              ],
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: const Color(0xFFF5F5F5),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: const Icon(Icons.account_balance_outlined,
                        size: 18, color: Color(0xFF999999)),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(e.institution,
                            style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 15,
                                color: Color(0xFF232F3E))),
                        const SizedBox(height: 2),
                        Text('${e.degree} · ${e.fieldOfStudy}',
                            style: const TextStyle(
                                fontSize: 14, color: Color(0xFF444444))),
                        if (e.startDate != null || e.endDate != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            _dateRange(e.startDate, e.endDate),
                            style: const TextStyle(
                                fontSize: 13, color: Color(0xFF999999)),
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
                      const Icon(Icons.open_in_new,
                          size: 14, color: Colors.indigo),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          link,
                          style: const TextStyle(
                              fontSize: 14,
                              color: Colors.indigo,
                              decoration: TextDecoration.underline,
                              decorationColor: Colors.indigo),
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
