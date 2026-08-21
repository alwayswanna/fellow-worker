import 'package:flutter/material.dart';

import '../../config/app_config.dart';
import '../../model/resume.dart';
import '../../model/user_profile.dart';
import '../../theme/app_theme.dart';
import '../../util/formatters.dart';
import '../../widgets/widgets.dart';

/// Left sidebar (wide layout) / drawer content (narrow layout) on
/// [ProfileScreen]: avatar, name, account info, edit/change-password
/// actions, plus an optional [extra] widget appended below (the EMPLOYER
/// role's compact resume section).
class ProfilePanel extends StatelessWidget {
  final UserProfile profile;
  final bool isUploadingPhoto;
  final VoidCallback onPickPhoto;
  final VoidCallback onEdit;
  final VoidCallback onChangePassword;
  final Widget? extra;

  const ProfilePanel({
    super.key,
    required this.profile,
    required this.isUploadingPhoto,
    required this.onPickPhoto,
    required this.onEdit,
    required this.onChangePassword,
    this.extra,
  });

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Avatar
          Center(
            child: GestureDetector(
              onTap: isUploadingPhoto ? null : onPickPhoto,
              child: Stack(
                children: [
                  CircleAvatar(
                    radius: 48,
                    backgroundColor: AppColors.accent,
                    backgroundImage: profile.photoUrl != null
                        ? NetworkImage(
                            '${AppConfig.photoBaseUrl}${profile.photoUrl}')
                        : null,
                    child: isUploadingPhoto
                        ? CircularProgressIndicator(color: AppColors.surface)
                        : profile.photoUrl == null
                            ? Text(
                                profile.firstName.isNotEmpty
                                    ? profile.firstName[0].toUpperCase()
                                    : '?',
                                style: TextStyle(
                                    fontSize: 36, color: AppColors.surface),
                              )
                            : null,
                  ),
                  Positioned(
                    bottom: 0,
                    right: 0,
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppColors.primary,
                        shape: BoxShape.circle,
                      ),
                      padding: const EdgeInsets.all(6),
                      child: Icon(Icons.camera_alt,
                          size: 16, color: AppColors.surface),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Name & login
          Center(
            child: Column(
              children: [
                Text(
                  profile.fullName,
                  style: const TextStyle(
                      fontSize: 20, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 4),
                Text(
                  '@${profile.login}',
                  style: TextStyle(color: AppColors.textSecondary),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
          Divider(color: AppColors.border),
          const SizedBox(height: 16),

          // Info rows
          _InfoRow(Icons.cake_outlined, 'Date of birth', profile.birthDate),
          if (profile.role != null)
            _InfoRow(
                Icons.shield_outlined, 'Role', profile.role!.displayName),
          if (profile.createdAt != null)
            _InfoRow(Icons.calendar_today_outlined, 'Registered',
                _fmt(profile.createdAt!)),
          if (profile.updatedAt != null)
            _InfoRow(Icons.edit_outlined, 'Last updated',
                _fmt(profile.updatedAt!)),
          const SizedBox(height: 24),

          // Edit profile button
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: onEdit,
              icon: const Icon(Icons.edit_outlined, size: 18),
              label: const Text('Edit profile'),
              style: OutlinedButton.styleFrom(
                foregroundColor: AppColors.primary,
                side: BorderSide(color: AppColors.primary),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          const SizedBox(height: 10),

          // Change password button
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: onChangePassword,
              icon: const Icon(Icons.lock_outline, size: 18),
              label: const Text('Change password'),
              style: OutlinedButton.styleFrom(
                foregroundColor: AppColors.textSecondary,
                side: BorderSide(color: AppColors.border),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          if (extra != null) ...[
            const SizedBox(height: 24),
            Divider(color: AppColors.border),
            const SizedBox(height: 4),
            extra!,
          ],
        ],
      ),
    );
  }

  String _fmt(String raw) {
    try {
      return formatDate(DateTime.parse(raw));
    } catch (_) {
      return raw;
    }
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _InfoRow(this.icon, this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppColors.textSecondary),
          const SizedBox(width: 10),
          Text('$label: ',
              style: TextStyle(color: AppColors.textSecondary, fontSize: 14)),
          Expanded(
            child: Text(value,
                style: const TextStyle(fontSize: 14),
                overflow: TextOverflow.ellipsis),
          ),
        ],
      ),
    );
  }
}

/// Compact "My resume" block appended to [ProfilePanel] for the EMPLOYER
/// role — create-hint when empty, otherwise a single summary card.
class ResumeSection extends StatelessWidget {
  final Resume? resume;
  final bool isLoading;
  final VoidCallback onCreate;
  final VoidCallback? onView;
  final VoidCallback? onEdit;

  const ResumeSection({
    super.key,
    required this.resume,
    required this.isLoading,
    required this.onCreate,
    this.onView,
    this.onEdit,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Text('My resume',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
            const Spacer(),
            if (resume == null && !isLoading)
              TextButton.icon(
                onPressed: onCreate,
                icon: const Icon(Icons.add, size: 16),
                label: const Text('Create'),
                style: TextButton.styleFrom(
                    foregroundColor: AppColors.primary,
                    padding: EdgeInsets.zero,
                    minimumSize: const Size(0, 0),
                    tapTargetSize: MaterialTapTargetSize.shrinkWrap),
              ),
          ],
        ),
        const SizedBox(height: 10),
        if (isLoading)
          const Center(
              child: SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2)))
        else if (resume == null)
          _EmptyResumeHint(onCreate: onCreate)
        else
          _CompactResumeCard(resume: resume!, onView: onView!, onEdit: onEdit!),
      ],
    );
  }
}

class _EmptyResumeHint extends StatelessWidget {
  final VoidCallback onCreate;

  const _EmptyResumeHint({required this.onCreate});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 20),
      decoration: BoxDecoration(
        color: AppColors.bg,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(
            color: AppColors.border, style: BorderStyle.solid),
      ),
      child: Column(
        children: [
          Icon(Icons.description_outlined,
              size: 36, color: AppColors.border),
          const SizedBox(height: 8),
          Text('No resume yet',
              style: TextStyle(fontSize: 13, color: AppColors.textSecondary)),
          const SizedBox(height: 10),
          FilledButton.icon(
            onPressed: onCreate,
            icon: const Icon(Icons.add, size: 16),
            label: const Text('Create resume'),
            style: FilledButton.styleFrom(
              backgroundColor: AppColors.accent,
              padding:
                  const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              textStyle: const TextStyle(fontSize: 13),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8)),
            ),
          ),
        ],
      ),
    );
  }
}

class _CompactResumeCard extends StatelessWidget {
  final Resume resume;
  final VoidCallback onView;
  final VoidCallback onEdit;

  const _CompactResumeCard({
    required this.resume,
    required this.onView,
    required this.onEdit,
  });

  @override
  Widget build(BuildContext context) {
    final title = resume.desiredPosition?.isNotEmpty == true
        ? resume.desiredPosition!
        : 'Resume';

    return Material(
      color: AppColors.tagBg,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onView,
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  InitialsAvatar(
                    name: resume.firstName,
                    radius: 18,
                    borderRadius: 8,
                    color: AppColors.avatarColor(resume.fullName).withAlpha(38),
                    textColor: AppColors.avatarColor(resume.fullName),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(title,
                            style: const TextStyle(
                                fontWeight: FontWeight.w600, fontSize: 13),
                            overflow: TextOverflow.ellipsis),
                        Text(resume.fullName,
                            style: TextStyle(
                                fontSize: 12, color: AppColors.textSecondary)),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: Icon(Icons.edit_outlined,
                        size: 16, color: AppColors.textSecondary),
                    tooltip: 'Edit',
                    onPressed: onEdit,
                    padding: EdgeInsets.zero,
                    constraints: const BoxConstraints(),
                  ),
                ],
              ),
              if (resume.skills.isNotEmpty) ...[
                const SizedBox(height: 8),
                Wrap(
                  spacing: 4,
                  runSpacing: 4,
                  children: resume.skills.take(4).map((s) => Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 7, vertical: 2),
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
      ),
    );
  }
}
