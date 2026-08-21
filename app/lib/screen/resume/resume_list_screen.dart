import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../service/resume_api_service.dart';
import '../../service/user_api_service.dart';
import 'resume_detail_screen.dart';
import 'resume_form_screen.dart';
import '../../theme/app_theme.dart';

class ResumeListScreen extends StatefulWidget {
  final ResumeApiService resumeApiService;
  final UserApiService userApiService;

  const ResumeListScreen({
    super.key,
    required this.resumeApiService,
    required this.userApiService,
  });

  @override
  State<ResumeListScreen> createState() => _ResumeListScreenState();
}

class _ResumeListScreenState extends State<ResumeListScreen> {
  List<Resume> _resumes = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    final resumes = await widget.resumeApiService.findAll();
    if (mounted) {
      setState(() {
        _resumes = resumes;
        _isLoading = false;
      });
    }
  }

  Future<void> _openForm({Resume? resume}) async {
    final updated = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => ResumeFormScreen(
          resumeApiService: widget.resumeApiService,
          userApiService: widget.userApiService,
          resume: resume,
        ),
      ),
    );
    if (updated == true) _load();
  }

  Future<void> _openDetail(Resume resume) async {
    final updated = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => ResumeDetailScreen(
          resume: resume,
          resumeApiService: widget.resumeApiService,
          onEdit: () => _openForm(resume: resume),
        ),
      ),
    );
    if (updated == true) _load();
  }

  Future<void> _confirmDelete(Resume resume) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        title: const Text('Delete resume?'),
        content: Text(
          'Resume "${resume.desiredPosition ?? resume.fullName}" will be permanently deleted.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            style: FilledButton.styleFrom(backgroundColor: AppColors.error),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirmed != true || !mounted) return;

    final ok = await widget.resumeApiService.deleteResume(resume.id);
    if (!mounted) return;
    if (ok) {
      _load();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Resume deleted')),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Failed to delete resume')),
      );
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
        shadowColor: Colors.black12,
        surfaceTintColor: AppColors.surface,
        title: const Text(
          'My Resumes',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
        ),
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
      ),
      body: _buildBody(),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        child: FilledButton.icon(
          onPressed: () => _openForm(),
          icon: const Icon(Icons.add),
          label: const Text('Create resume',
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

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_error != null) {
      return _ErrorState(message: _error!, onRetry: _load);
    }
    if (_resumes.isEmpty) {
      return _EmptyState(onCreateTap: () => _openForm());
    }
    return RefreshIndicator(
      onRefresh: _load,
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
        itemCount: _resumes.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (_, i) => _ResumeCard(
          resume: _resumes[i],
          onTap: () => _openDetail(_resumes[i]),
          onEdit: () => _openForm(resume: _resumes[i]),
          onDelete: () => _confirmDelete(_resumes[i]),
        ),
      ),
    );
  }
}

// ── Resume card ────────────────────────────────────────────────────────────

class _ResumeCard extends StatelessWidget {
  final Resume resume;
  final VoidCallback onTap;
  final VoidCallback onEdit;
  final VoidCallback onDelete;

  const _ResumeCard({
    required this.resume,
    required this.onTap,
    required this.onEdit,
    required this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final title = resume.desiredPosition?.isNotEmpty == true
        ? resume.desiredPosition!
        : 'Resume';

    return Material(
      color: AppColors.surface,
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header row: avatar + title + active chip
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _Avatar(name: resume.firstName),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          title,
                          style: const TextStyle(
                            fontSize: 17,
                            fontWeight: FontWeight.bold,
                            color: AppColors.text,
                          ),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          resume.fullName,
                          style: const TextStyle(
                              fontSize: 14, color: AppColors.textSecondary),
                        ),
                      ],
                    ),
                  ),
                  const _ActiveChip(),
                ],
              ),

              const SizedBox(height: 12),

              // Meta row: email + experience count
              _MetaRow(resume: resume),

              // Skills
              if (resume.skills.isNotEmpty) ...[
                const SizedBox(height: 10),
                _SkillsRow(skills: resume.skills),
              ],

              const Padding(
                padding: EdgeInsets.symmetric(vertical: 12),
                child: Divider(height: 1, color: AppColors.bg),
              ),

              // Action buttons
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: onEdit,
                      icon: const Icon(Icons.edit_outlined, size: 16),
                      label: const Text('Edit'),
                      style: OutlinedButton.styleFrom(
                        foregroundColor: AppColors.primary,
                        side: const BorderSide(color: AppColors.primary),
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(8)),
                        padding: const EdgeInsets.symmetric(vertical: 10),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  OutlinedButton.icon(
                    onPressed: onDelete,
                    icon: const Icon(Icons.delete_outline, size: 16),
                    label: const Text('Delete'),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: AppColors.error,
                      side: const BorderSide(color: AppColors.error),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8)),
                      padding: const EdgeInsets.symmetric(
                          vertical: 10, horizontal: 16),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _Avatar extends StatelessWidget {
  final String name;
  const _Avatar({required this.name});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 48,
      height: 48,
      decoration: BoxDecoration(
        color: AppColors.tagBg,
        borderRadius: BorderRadius.circular(10),
      ),
      alignment: Alignment.center,
      child: Text(
        name.isNotEmpty ? name[0].toUpperCase() : '?',
        style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.bold,
            color: AppColors.tagText),
      ),
    );
  }
}

class _ActiveChip extends StatelessWidget {
  const _ActiveChip();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: AppColors.successBg,
        borderRadius: BorderRadius.circular(20),
      ),
      child: const Text(
        'Active',
        style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w600,
            color: AppColors.success),
      ),
    );
  }
}

class _MetaRow extends StatelessWidget {
  final Resume resume;
  const _MetaRow({required this.resume});

  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 16,
      runSpacing: 4,
      children: [
        _meta(Icons.email_outlined, resume.email),
        if (resume.experience.isNotEmpty)
          _meta(Icons.work_outline,
              '${resume.experience.length} job${resume.experience.length > 1 ? 's' : ''}'),
        if (resume.education.isNotEmpty)
          _meta(Icons.school_outlined,
              '${resume.education.length} education'),
      ],
    );
  }

  Widget _meta(IconData icon, String text) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 14, color: AppColors.textSecondary),
        const SizedBox(width: 4),
        Text(text,
            style:
                const TextStyle(fontSize: 13, color: AppColors.textSecondary)),
      ],
    );
  }
}

class _SkillsRow extends StatelessWidget {
  final List<String> skills;
  const _SkillsRow({required this.skills});

  @override
  Widget build(BuildContext context) {
    final shown = skills.take(4).toList();
    final extra = skills.length - shown.length;
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: [
        ...shown.map((s) => _SkillTag(label: s)),
        if (extra > 0)
          _SkillTag(label: '+$extra', muted: true),
      ],
    );
  }
}

class _SkillTag extends StatelessWidget {
  final String label;
  final bool muted;
  const _SkillTag({required this.label, this.muted = false});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: muted ? AppColors.bg : AppColors.tagBg,
        borderRadius: BorderRadius.circular(6),
      ),
      child: Text(
        label,
        style: TextStyle(
          fontSize: 12,
          color: muted ? AppColors.textSecondary : AppColors.tagText,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }
}

// ── States ─────────────────────────────────────────────────────────────────

class _EmptyState extends StatelessWidget {
  final VoidCallback onCreateTap;
  const _EmptyState({required this.onCreateTap});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 96,
              height: 96,
              decoration: BoxDecoration(
                color: AppColors.tagBg,
                shape: BoxShape.circle,
              ),
              child: Icon(Icons.description_outlined,
                  size: 48, color: AppColors.border),
            ),
            const SizedBox(height: 24),
            const Text(
              'No resumes yet',
              style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: AppColors.text),
            ),
            const SizedBox(height: 8),
            const Text(
              'Create your first resume and start applying for jobs',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 14, color: AppColors.textSecondary),
            ),
            const SizedBox(height: 32),
            FilledButton.icon(
              onPressed: onCreateTap,
              icon: const Icon(Icons.add),
              label: const Text('Create resume'),
              style: FilledButton.styleFrom(
                backgroundColor: AppColors.accent,
                padding:
                    const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ErrorState extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;
  const _ErrorState({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 48, color: AppColors.error),
          const SizedBox(height: 16),
          Text(message, style: const TextStyle(color: AppColors.error)),
          const SizedBox(height: 16),
          OutlinedButton(onPressed: onRetry, child: const Text('Retry')),
        ],
      ),
    );
  }
}
