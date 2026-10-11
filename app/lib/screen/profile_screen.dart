import 'dart:convert';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../model/resume.dart';
import '../model/user_profile.dart';
import '../service/auth_service.dart';
import '../service/resume_api_service.dart';
import '../service/user_api_service.dart';
import '../service/vacancy_api_service.dart';
import '../theme/app_theme.dart';
import 'profile/applicant_tab_panel.dart';
import 'profile/company_owner_panel.dart';
import 'profile/profile_panel.dart';
import 'profile/recruiter_panel.dart';
import 'profile/resumes_panel.dart';
import 'resume/resume_detail_screen.dart';
import 'resume/resume_form_screen.dart';
import 'vacancy/vacancy_browse_screen.dart' show ThemeToggleButton;

class ProfileScreen extends StatefulWidget {
  final AuthService authService;
  final UserApiService userApiService;
  final ResumeApiService resumeApiService;
  final VacancyApiService vacancyApiService;

  const ProfileScreen({
    super.key,
    required this.authService,
    required this.userApiService,
    required this.resumeApiService,
    required this.vacancyApiService,
  });

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  UserProfile? _profile;
  List<Resume> _resumes = [];
  bool _isLoading = true;
  bool _isResumesLoading = true;
  bool _isUploadingPhoto = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    final profile = await widget.userApiService.getMyProfile();
    if (mounted) {
      setState(() {
        _profile = profile;
        _error = profile == null ? 'Failed to load profile' : null;
        _isLoading = false;
      });
      if (profile != null) _loadResumes();
    }
  }

  Future<void> _loadResumes() async {
    setState(() => _isResumesLoading = true);
    final resumes = await widget.resumeApiService.findAll();
    if (mounted) {
      setState(() {
        _resumes = resumes;
        _isResumesLoading = false;
      });
    }
  }

  // ── Photo ────────────────────────────────────────────────────────────────

  void _pickAndUploadPhoto() {
    final input = html.FileUploadInputElement()..accept = 'image/*';
    input.click();
    input.onChange.listen((_) {
      if (input.files == null || input.files!.isEmpty) return;
      final file = input.files!.first;
      final reader = html.FileReader();
      reader.readAsDataUrl(file);
      reader.onLoadEnd.listen((_) {
        if (reader.result != null && mounted) {
          final dataUrl = reader.result as String;
          final base64Data = dataUrl.split(',').last;
          _uploadPhoto(base64Decode(base64Data), file.name, file.type);
        }
      });
    });
  }

  Future<void> _uploadPhoto(
    Uint8List bytes,
    String filename,
    String? contentType,
  ) async {
    setState(() => _isUploadingPhoto = true);
    final updated =
        await widget.userApiService.uploadPhoto(bytes, filename, contentType);
    if (mounted) {
      setState(() {
        _isUploadingPhoto = false;
        if (updated != null) _profile = updated;
      });
      if (updated == null) {
        _showSnack('Failed to upload photo');
      }
    }
  }

  // ── Edit profile ─────────────────────────────────────────────────────────

  Future<void> _showEditDialog() async {
    if (_profile == null) return;

    final firstNameCtrl = TextEditingController(text: _profile!.firstName);
    final lastNameCtrl = TextEditingController(text: _profile!.lastName);
    final birthDateCtrl = TextEditingController(text: _profile!.birthDate);
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        title: const Text('Edit profile'),
        content: SizedBox(
          width: 400,
          child: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                _dialogField(firstNameCtrl, 'First name'),
                const SizedBox(height: 12),
                _dialogField(lastNameCtrl, 'Last name'),
                const SizedBox(height: 12),
                TextFormField(
                  controller: birthDateCtrl,
                  decoration: _inputDecoration('Date of birth (YYYY-MM-DD)'),
                  validator: (v) {
                    if (v == null || v.isEmpty) return 'Required';
                    if (!RegExp(r'^\d{4}-\d{2}-\d{2}$').hasMatch(v)) {
                      return 'Use format YYYY-MM-DD';
                    }
                    return null;
                  },
                ),
              ],
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            style: FilledButton.styleFrom(backgroundColor: AppColors.accent),
            onPressed: () {
              if (formKey.currentState!.validate()) Navigator.pop(ctx, true);
            },
            child: const Text('Save'),
          ),
        ],
      ),
    );

    if (confirmed != true || !mounted) return;

    final updated = await widget.userApiService.updateProfile(
      firstName: firstNameCtrl.text.trim(),
      lastName: lastNameCtrl.text.trim(),
      birthDate: birthDateCtrl.text.trim(),
    );

    if (mounted) {
      if (updated != null) {
        setState(() => _profile = updated);
      } else {
        _showSnack('Failed to update profile');
      }
    }
  }

  Future<void> _showChangePasswordDialog() async {
    final oldPasswordCtrl = TextEditingController();
    final newPasswordCtrl = TextEditingController();
    final confirmPasswordCtrl = TextEditingController();
    final formKey = GlobalKey<FormState>();
    String? serverError;

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          title: const Text('Change password'),
          content: SizedBox(
            width: 400,
            child: Form(
              key: formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (serverError != null) ...[
                    Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        color: AppColors.errorBg,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: AppColors.error.withAlpha(80)),
                      ),
                      child: Row(
                        children: [
                          Icon(Icons.error_outline,
                              size: 16, color: AppColors.error),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(serverError!,
                                style: TextStyle(
                                    color: AppColors.error, fontSize: 13)),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 12),
                  ],
                  TextFormField(
                    controller: oldPasswordCtrl,
                    decoration: _inputDecoration('Current password'),
                    obscureText: true,
                    validator: (v) =>
                        (v == null || v.isEmpty) ? 'Required' : null,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: newPasswordCtrl,
                    decoration: _inputDecoration('New password'),
                    obscureText: true,
                    validator: (v) {
                      if (v == null || v.isEmpty) return 'Required';
                      if (v.length < 6) return 'At least 6 characters';
                      return null;
                    },
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: confirmPasswordCtrl,
                    decoration: _inputDecoration('Confirm new password'),
                    obscureText: true,
                    validator: (v) {
                      if (v == null || v.isEmpty) return 'Required';
                      if (v != newPasswordCtrl.text) {
                        return 'Passwords do not match';
                      }
                      return null;
                    },
                  ),
                ],
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel'),
            ),
            FilledButton(
              style: FilledButton.styleFrom(backgroundColor: AppColors.accent),
              onPressed: () async {
                setDialogState(() => serverError = null);
                if (!formKey.currentState!.validate()) return;

                final error = await widget.userApiService.changePassword(
                  oldPassword: oldPasswordCtrl.text,
                  newPassword: newPasswordCtrl.text,
                  confirmPassword: confirmPasswordCtrl.text,
                );

                if (error == null) {
                  Navigator.pop(ctx, true);
                } else {
                  setDialogState(() => serverError = error);
                }
              },
              child: const Text('Change'),
            ),
          ],
        ),
      ),
    );

    if (confirmed == true && mounted) {
      _showSnack('Password changed successfully');
    }
  }

  // ── Resumes ──────────────────────────────────────────────────────────────

  Future<void> _openCreateResume() async {
    final created = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => ResumeFormScreen(
          resumeApiService: widget.resumeApiService,
          userApiService: widget.userApiService,
        ),
      ),
    );
    if (created == true) _loadResumes();
  }

  Future<void> _openResumeDetail(Resume resume) async {
    final updated = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => ResumeDetailScreen(
          resume: resume,
          resumeApiService: widget.resumeApiService,
          onEdit: () => _openEditResume(resume),
        ),
      ),
    );
    if (updated == true) _loadResumes();
  }

  Future<void> _openEditResume(Resume resume) async {
    final updated = await Navigator.of(context).push<bool>(
      MaterialPageRoute(
        builder: (_) => ResumeFormScreen(
          resumeApiService: widget.resumeApiService,
          userApiService: widget.userApiService,
          resume: resume,
        ),
      ),
    );
    if (updated == true) _loadResumes();
  }

  // ── Misc ─────────────────────────────────────────────────────────────────

  Future<void> _onLogout() async {
    await widget.authService.logout();
    if (mounted) Navigator.of(context).pushReplacementNamed('/login');
  }

  void _showSnack(String msg) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(msg)));
  }

  // ── Build ─────────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    final isWide = MediaQuery.of(context).size.width >= 700;
    final showDrawer = !isWide && _profile != null && _error == null;
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
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
        actions: [
          const ThemeToggleButton(),
          if (_profile?.role?.code == 'ADMIN')
            IconButton(
              icon: const Icon(Icons.admin_panel_settings_outlined),
              tooltip: 'Admin panel',
              onPressed: () => Navigator.of(context).pushNamed('/admin'),
            ),
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Sign out',
            onPressed: _onLogout,
          ),
        ],
      ),
      // On narrow viewports the profile/account panel moves into a drawer
      // (Scaffold auto-adds the hamburger icon) instead of stacking above
      // the tab content, which used to push everything below the fold.
      drawer: showDrawer ? Drawer(child: _buildLeftPanel(_profile!)) : null,
      body: _buildBody(isWide),
    );
  }

  Widget _buildBody(bool isWide) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_error!, style: TextStyle(color: AppColors.error)),
            const SizedBox(height: 16),
            ElevatedButton(onPressed: _loadProfile, child: const Text('Retry')),
          ],
        ),
      );
    }
    return _buildContent(_profile!, isWide);
  }

  Widget _buildLeftPanel(UserProfile profile) {
    return ProfilePanel(
      profile: profile,
      isUploadingPhoto: _isUploadingPhoto,
      onPickPhoto: _pickAndUploadPhoto,
      onEdit: _showEditDialog,
      onChangePassword: _showChangePasswordDialog,
    );
  }

  Widget _buildContent(UserProfile profile, bool isWide) {
    final isApplicant = profile.role?.code == 'APPLICANT';
    final isCompany = profile.role?.code == 'COMPANY_FOUNDER';
    final isRecruiter = profile.role?.code == 'RECRUITER';

    Widget buildRightPanel() {
      if (isApplicant) {
        return ApplicantTabPanel(
          vacancyApiService: widget.vacancyApiService,
          userRole: profile.role?.code,
          resumes: _resumes,
          isResumesLoading: _isResumesLoading,
          onAddResume: _openCreateResume,
          onTapResume: _openResumeDetail,
          onEditResume: _openEditResume,
        );
      }
      if (isCompany) {
        return CompanyOwnerPanel(
          vacancyApiService: widget.vacancyApiService,
          userApiService: widget.userApiService,
          resumeApiService: widget.resumeApiService,
        );
      }
      if (isRecruiter) {
        return RecruiterPanel(
          vacancyApiService: widget.vacancyApiService,
          userApiService: widget.userApiService,
          resumeApiService: widget.resumeApiService,
        );
      }
      return ResumesPanel(
        resumes: _resumes,
        isLoading: _isResumesLoading,
        onAdd: _openCreateResume,
        onTap: _openResumeDetail,
        onEdit: _openEditResume,
      );
    }

    if (isWide) {
      return Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 320, child: _buildLeftPanel(profile)),
          VerticalDivider(width: 1, color: AppColors.border),
          Expanded(child: buildRightPanel()),
        ],
      );
    }

    // Narrow viewport: the profile/account panel lives in the Scaffold's
    // drawer (see build()), so the tab content gets the full width here.
    return buildRightPanel();
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  Widget _dialogField(TextEditingController ctrl, String label) {
    return TextFormField(
      controller: ctrl,
      decoration: _inputDecoration(label),
      validator: (v) =>
          (v == null || v.trim().isEmpty) ? 'Required' : null,
    );
  }

  InputDecoration _inputDecoration(String label) => InputDecoration(
        labelText: label,
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
      );
}
