import 'package:flutter/material.dart';

import '../../config/app_config.dart';
import '../../model/user_profile.dart';
import '../../theme/app_theme.dart';
import '../../util/formatters.dart';

/// Left sidebar (wide layout) / drawer content (narrow layout) on
/// [ProfileScreen]: avatar, name, account info, edit/change-password actions.
class ProfilePanel extends StatelessWidget {
  final UserProfile profile;
  final bool isUploadingPhoto;
  final VoidCallback onPickPhoto;
  final VoidCallback onEdit;
  final VoidCallback onChangePassword;

  const ProfilePanel({
    super.key,
    required this.profile,
    required this.isUploadingPhoto,
    required this.onPickPhoto,
    required this.onEdit,
    required this.onChangePassword,
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
