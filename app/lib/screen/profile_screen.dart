import 'dart:convert';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../config/app_config.dart';
import '../model/application.dart';
import '../model/resume.dart';
import '../model/user_profile.dart';
import '../model/vacancy.dart';
import '../service/auth_service.dart';
import '../service/resume_api_service.dart';
import '../service/user_api_service.dart';
import '../service/vacancy_api_service.dart';
import '../util/formatters.dart';
import '../widgets/widgets.dart';
import 'resume/resume_detail_screen.dart';
import 'resume/resume_form_screen.dart';

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
          _uploadPhoto(base64Decode(base64Data), file.name);
        }
      });
    });
  }

  Future<void> _uploadPhoto(Uint8List bytes, String filename) async {
    setState(() => _isUploadingPhoto = true);
    final updated = await widget.userApiService.uploadPhoto(bytes, filename);
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
            style: FilledButton.styleFrom(backgroundColor: Colors.indigo),
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
                        color: Colors.red.shade50,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.red.shade200),
                      ),
                      child: Row(
                        children: [
                          Icon(Icons.error_outline,
                              size: 16, color: Colors.red.shade700),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(serverError!,
                                style: TextStyle(
                                    color: Colors.red.shade700, fontSize: 13)),
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
              style: FilledButton.styleFrom(backgroundColor: Colors.indigo),
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
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF232F3E),
        elevation: 0,
        surfaceTintColor: Colors.white,
        title: const Text(
          'Fellow Worker',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
        ),
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: Color(0xFFE0E0E0)),
        ),
        actions: [
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
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(onPressed: _loadProfile, child: const Text('Retry')),
          ],
        ),
      );
    }
    return _buildContent(_profile!);
  }

  Widget _buildContent(UserProfile profile) {
    final isEmployer = profile.role?.code == 'EMPLOYER';
    final isApplicant = profile.role?.code == 'APPLICANT';
    final isCompany = profile.role?.code == 'COMPANY';
    final isRecruiter = profile.role?.code == 'RECRUITER';
    final resume = _resumes.firstOrNull;

    final resumeSection = _ResumeSection(
      resume: resume,
      isLoading: _isResumesLoading,
      onCreate: _openCreateResume,
      onView: resume != null ? () => _openResumeDetail(resume) : null,
      onEdit: resume != null ? () => _openEditResume(resume) : null,
    );

    final leftPanel = _ProfilePanel(
      profile: profile,
      isUploadingPhoto: _isUploadingPhoto,
      onPickPhoto: _pickAndUploadPhoto,
      onEdit: _showEditDialog,
      onChangePassword: _showChangePasswordDialog,
      extra: isEmployer ? resumeSection : null,
    );

    Widget buildRightPanel() {
      if (isEmployer) {
        return _JobSearchPanel(
          vacancyApiService: widget.vacancyApiService,
          userRole: profile.role?.code,
        );
      }
      if (isApplicant) {
        return _ApplicantTabPanel(
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
        return _CompanyOwnerPanel(
          vacancyApiService: widget.vacancyApiService,
          userApiService: widget.userApiService,
        );
      }
      if (isRecruiter) {
        return _RecruiterPanel(
          vacancyApiService: widget.vacancyApiService,
          userApiService: widget.userApiService,
        );
      }
      return _ResumesPanel(
        resumes: _resumes,
        isLoading: _isResumesLoading,
        onAdd: _openCreateResume,
        onTap: _openResumeDetail,
        onEdit: _openEditResume,
      );
    }

    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth >= 700;

        if (isWide) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SizedBox(width: 320, child: leftPanel),
              const VerticalDivider(width: 1, color: Color(0xFFE0E0E0)),
              Expanded(child: buildRightPanel()),
            ],
          );
        }

        // Narrow layout — panels with Expanded right side for employer/applicant/company
        if (isEmployer || isApplicant || isCompany || isRecruiter) {
          return Column(
            children: [
              ConstrainedBox(
                constraints: BoxConstraints(
                    maxHeight: MediaQuery.of(context).size.height * 0.45),
                child: leftPanel,
              ),
              const Divider(height: 1, color: Color(0xFFE0E0E0)),
              Expanded(child: buildRightPanel()),
            ],
          );
        }

        return SingleChildScrollView(
          child: Column(
            children: [
              leftPanel,
              const Divider(height: 1, color: Color(0xFFE0E0E0)),
              buildRightPanel(),
            ],
          ),
        );
      },
    );
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

// ── Profile panel ─────────────────────────────────────────────────────────────

class _ProfilePanel extends StatelessWidget {
  final UserProfile profile;
  final bool isUploadingPhoto;
  final VoidCallback onPickPhoto;
  final VoidCallback onEdit;
  final VoidCallback onChangePassword;
  final Widget? extra;

  const _ProfilePanel({
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
                    backgroundColor: Colors.indigo,
                    backgroundImage: profile.photoUrl != null
                        ? NetworkImage(
                            '${AppConfig.photoBaseUrl}${profile.photoUrl}')
                        : null,
                    child: isUploadingPhoto
                        ? const CircularProgressIndicator(color: Colors.white)
                        : profile.photoUrl == null
                            ? Text(
                                profile.firstName.isNotEmpty
                                    ? profile.firstName[0].toUpperCase()
                                    : '?',
                                style: const TextStyle(
                                    fontSize: 36, color: Colors.white),
                              )
                            : null,
                  ),
                  Positioned(
                    bottom: 0,
                    right: 0,
                    child: Container(
                      decoration: const BoxDecoration(
                        color: Colors.indigo,
                        shape: BoxShape.circle,
                      ),
                      padding: const EdgeInsets.all(6),
                      child: const Icon(Icons.camera_alt,
                          size: 16, color: Colors.white),
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
                  style: const TextStyle(color: Color(0xFF888888)),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
          const Divider(color: Color(0xFFE0E0E0)),
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
                foregroundColor: Colors.indigo,
                side: const BorderSide(color: Colors.indigo),
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
                foregroundColor: const Color(0xFF555555),
                side: const BorderSide(color: Color(0xFFCCCCCC)),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          if (extra != null) ...[
            const SizedBox(height: 24),
            const Divider(color: Color(0xFFE0E0E0)),
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
          Icon(icon, size: 18, color: const Color(0xFF888888)),
          const SizedBox(width: 10),
          Text('$label: ',
              style: const TextStyle(color: Color(0xFF888888), fontSize: 14)),
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

// ── Job search panel (read-only vacancy browser for job seekers) ───────────────

class _JobSearchPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;

  const _JobSearchPanel({required this.vacancyApiService, this.userRole});

  @override
  State<_JobSearchPanel> createState() => _JobSearchPanelState();
}

class _JobSearchPanelState extends State<_JobSearchPanel> {
  final _searchCtrl = TextEditingController();
  bool _isLoading = false;
  bool _firstLoad = false;
  List<Vacancy> _vacancies = [];

  EmploymentType? _typeFilter;
  WorkFormat? _formatFilter;
  ExperienceLevel? _levelFilter;

  @override
  void initState() {
    super.initState();
    _search();
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    setState(() {
      _isLoading = true;
      if (!_firstLoad) _firstLoad = true;
    });
    final results = await widget.vacancyApiService.searchVacancies(
      title: _searchCtrl.text.trim().isEmpty ? null : _searchCtrl.text.trim(),
      employmentType: _typeFilter,
      workFormat: _formatFilter,
      experienceLevel: _levelFilter,
    );
    if (mounted) {
      setState(() {
        _vacancies = results;
        _isLoading = false;
      });
    }
  }

  void _showVacancyDetail(Vacancy v) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _VacancyDetailSheet(
        vacancy: v,
        vacancyApiService: widget.vacancyApiService,
        userRole: widget.userRole,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Search bar
        Container(
          color: Colors.white,
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
          child: Column(
            children: [
              TextField(
                controller: _searchCtrl,
                decoration: InputDecoration(
                  hintText: 'Job title, skill, company...',
                  prefixIcon: const Icon(Icons.search, color: Color(0xFF888888)),
                  suffixIcon: _searchCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _searchCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: const Color(0xFFF5F5F5),
                ),
                onSubmitted: (_) => _search(),
              ),
              const SizedBox(height: 10),
              // Filter chips
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    _FilterPopup<EmploymentType?>(
                      label: _typeFilter?.displayName ?? 'Employment',
                      isActive: _typeFilter != null,
                      options: [
                        (null, 'Any'),
                        ...EmploymentType.values.map((e) => (e, e.displayName)),
                      ],
                      value: _typeFilter,
                      onChanged: (v) {
                        setState(() => _typeFilter = v);
                        _search();
                      },
                    ),
                    const SizedBox(width: 8),
                    _FilterPopup<WorkFormat?>(
                      label: _formatFilter?.displayName ?? 'Format',
                      isActive: _formatFilter != null,
                      options: [
                        (null, 'Any'),
                        ...WorkFormat.values.map((e) => (e, e.displayName)),
                      ],
                      value: _formatFilter,
                      onChanged: (v) {
                        setState(() => _formatFilter = v);
                        _search();
                      },
                    ),
                    const SizedBox(width: 8),
                    _FilterPopup<ExperienceLevel?>(
                      label: _levelFilter?.displayName ?? 'Experience',
                      isActive: _levelFilter != null,
                      options: [
                        (null, 'Any'),
                        ...ExperienceLevel.values.map((e) => (e, e.displayName)),
                      ],
                      value: _levelFilter,
                      onChanged: (v) {
                        setState(() => _levelFilter = v);
                        _search();
                      },
                    ),
                    if (_typeFilter != null || _formatFilter != null || _levelFilter != null) ...[
                      const SizedBox(width: 8),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            _typeFilter = null;
                            _formatFilter = null;
                            _levelFilter = null;
                          });
                          _search();
                        },
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 10, vertical: 7),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(20),
                            border: Border.all(color: const Color(0xFFDDDDDD)),
                          ),
                          child: const Text('Clear all',
                              style: TextStyle(
                                  fontSize: 13, color: Color(0xFF888888))),
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              const SizedBox(height: 8),
            ],
          ),
        ),
        const Divider(height: 1, color: Color(0xFFE0E0E0)),
        // Results
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _vacancies.isEmpty
                  ? EmptyState(
                      icon: Icons.search_off_outlined,
                      title: 'No vacancies found',
                      subtitle: 'Try different keywords or filters',
                      action: OutlinedButton(
                        onPressed: () {
                          _searchCtrl.clear();
                          setState(() {
                            _typeFilter = null;
                            _formatFilter = null;
                            _levelFilter = null;
                          });
                          _search();
                        },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.indigo,
                          side: const BorderSide(color: Colors.indigo),
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(8)),
                        ),
                        child: const Text('Reset filters'),
                      ),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.all(16),
                      itemCount: _vacancies.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (_, i) => _VacancyBrowseCard(
                        vacancy: _vacancies[i],
                        onTap: () => _showVacancyDetail(_vacancies[i]),
                      ),
                    ),
        ),
      ],
    );
  }
}

class _FilterPopup<T> extends StatelessWidget {
  final String label;
  final bool isActive;
  final List<(T, String)> options;
  final T value;
  final void Function(T) onChanged;

  const _FilterPopup({
    required this.label,
    required this.isActive,
    required this.options,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return PopupMenuButton<T>(
      initialValue: value,
      onSelected: onChanged,
      itemBuilder: (_) => options
          .map((opt) => PopupMenuItem<T>(value: opt.$1, child: Text(opt.$2)))
          .toList(),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
        decoration: BoxDecoration(
          color: isActive ? Colors.indigo.shade50 : Colors.transparent,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: isActive ? Colors.indigo.shade300 : const Color(0xFFDDDDDD),
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(label,
                style: TextStyle(
                    fontSize: 13,
                    color:
                        isActive ? Colors.indigo : const Color(0xFF555555))),
            const SizedBox(width: 4),
            Icon(Icons.arrow_drop_down,
                size: 18,
                color: isActive ? Colors.indigo : const Color(0xFF888888)),
          ],
        ),
      ),
    );
  }
}

class _VacancyBrowseCard extends StatelessWidget {
  final Vacancy vacancy;
  final VoidCallback onTap;

  const _VacancyBrowseCard({required this.vacancy, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      color: Colors.indigo.shade50,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Text(
                      (vacancy.company?.name ?? '').isNotEmpty
                          ? vacancy.company!.name[0].toUpperCase()
                          : '?',
                      style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: Colors.indigo.shade600),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(vacancy.title,
                            style: const TextStyle(
                                fontWeight: FontWeight.w600, fontSize: 15)),
                        Text(vacancy.company?.name ?? '',
                            style: const TextStyle(
                                fontSize: 13, color: Color(0xFF666666))),
                      ],
                    ),
                  ),
                  const Icon(Icons.chevron_right,
                      size: 20, color: Color(0xFFCCCCCC)),
                ],
              ),
              const SizedBox(height: 10),
              Wrap(
                spacing: 8,
                runSpacing: 6,
                children: [
                  if (_salary != null)
                    _MetaTag(Icons.payments_outlined, _salary!),
                  if (vacancy.city != null)
                    _MetaTag(Icons.location_on_outlined, vacancy.city!),
                  if (vacancy.employmentType != null)
                    _MetaTag(Icons.access_time_outlined,
                        vacancy.employmentType!.displayName),
                  if (vacancy.workFormat != null)
                    _MetaTag(
                        Icons.laptop_outlined, vacancy.workFormat!.displayName),
                  if (vacancy.experienceLevel != null)
                    _MetaTag(Icons.bar_chart_outlined,
                        vacancy.experienceLevel!.displayName),
                ],
              ),
              if (vacancy.skills.isNotEmpty) ...[
                const SizedBox(height: 8),
                Wrap(
                  spacing: 6,
                  runSpacing: 6,
                  children: vacancy.skills.take(5).map((s) => Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(
                          color: const Color(0xFFEEF2FF),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(s,
                            style: TextStyle(
                                fontSize: 12,
                                color: Colors.indigo.shade700)),
                      )).toList(),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  String? get _salary =>
      formatSalary(vacancy.salaryFrom, vacancy.salaryTo, vacancy.currency);
}

class _MetaTag extends StatelessWidget {
  final IconData icon;
  final String label;

  const _MetaTag(this.icon, this.label);

  @override
  Widget build(BuildContext context) => Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: const Color(0xFF888888)),
          const SizedBox(width: 4),
          Text(label,
              style:
                  const TextStyle(fontSize: 13, color: Color(0xFF555555))),
        ],
      );
}

// ── Vacancy detail sheet ───────────────────────────────────────────────────────

class _VacancyDetailSheet extends StatefulWidget {
  final Vacancy vacancy;
  final String? userRole;
  final VacancyApiService? vacancyApiService;

  const _VacancyDetailSheet({
    required this.vacancy,
    this.userRole,
    this.vacancyApiService,
  });

  @override
  State<_VacancyDetailSheet> createState() => _VacancyDetailSheetState();
}

class _VacancyDetailSheetState extends State<_VacancyDetailSheet> {
  Application? _myApplication;
  Company? _company;
  bool _isApplying = false;

  bool get _isApplicant =>
      widget.userRole == 'APPLICANT' || widget.userRole == 'EMPLOYER';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final api = widget.vacancyApiService;
    if (api == null) return;
    final futures = <Future>[
      if (_isApplicant) api.getMyApplicationForVacancy(widget.vacancy.id),
      api.getCompanyById(widget.vacancy.companyId),
    ];
    final results = await Future.wait(futures);
    if (!mounted) return;
    setState(() {
      int idx = 0;
      if (_isApplicant) { _myApplication = results[idx++] as Application?; }
      _company = results[idx] as Company?;
    });
  }

  Future<void> _apply() async {
    setState(() => _isApplying = true);
    final result = await widget.vacancyApiService!.applyToVacancy(widget.vacancy.id);
    if (mounted) setState(() { _myApplication = result; _isApplying = false; });
  }

  Future<void> _withdraw() async {
    setState(() => _isApplying = true);
    final ok = await widget.vacancyApiService!.withdrawApplication(widget.vacancy.id);
    if (mounted && ok) {
      final updated = await widget.vacancyApiService!.getMyApplicationForVacancy(widget.vacancy.id);
      setState(() { _myApplication = updated; _isApplying = false; });
    } else if (mounted) {
      setState(() => _isApplying = false);
    }
  }

  void _viewCompany() {
    final company = _company;
    if (company == null) return;
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _CompanyDetailSheet(company: company),
    );
  }

  @override
  Widget build(BuildContext context) {
    final vacancy = widget.vacancy;
    return DraggableScrollableSheet(
      initialChildSize: 0.75,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, scrollCtrl) => Container(
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          children: [
            const BottomSheetHandle(),
            Expanded(
              child: ListView(
                controller: scrollCtrl,
                padding: const EdgeInsets.fromLTRB(24, 8, 24, 32),
                children: [
                  // Header
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: 52,
                        height: 52,
                        decoration: BoxDecoration(
                          color: Colors.indigo.shade50,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          (vacancy.company?.name ?? _company?.name ?? '').isNotEmpty
                              ? (vacancy.company?.name ?? _company?.name ?? '?')[0].toUpperCase()
                              : '?',
                          style: TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: Colors.indigo.shade600),
                        ),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(vacancy.title,
                                style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold)),
                            const SizedBox(height: 2),
                            if ((vacancy.company?.name ?? _company?.name) != null)
                              Text(vacancy.company?.name ?? _company!.name,
                                  style: const TextStyle(
                                      fontSize: 14,
                                      color: Color(0xFF555555))),
                            if ((vacancy.company?.city ?? _company?.city) != null)
                              Text(vacancy.company?.city ?? _company!.city!,
                                  style: const TextStyle(
                                      fontSize: 13,
                                      color: Color(0xFF888888))),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  // Meta chips
                  Wrap(
                    spacing: 10,
                    runSpacing: 8,
                    children: [
                      if (_salary != null)
                        _DetailChip(Icons.payments_outlined, _salary!,
                            color: const Color(0xFF16A34A)),
                      if (vacancy.city != null)
                        _DetailChip(Icons.location_on_outlined, vacancy.city!),
                      if (vacancy.employmentType != null)
                        _DetailChip(Icons.access_time_outlined,
                            vacancy.employmentType!.displayName),
                      if (vacancy.workFormat != null)
                        _DetailChip(Icons.laptop_outlined,
                            vacancy.workFormat!.displayName),
                      if (vacancy.experienceLevel != null)
                        _DetailChip(Icons.bar_chart_outlined,
                            vacancy.experienceLevel!.displayName),
                    ],
                  ),
                  if (vacancy.skills.isNotEmpty) ...[
                    const SizedBox(height: 16),
                    const Text('Skills',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: vacancy.skills
                          .map((s) => Container(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: 10, vertical: 4),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFEEF2FF),
                                  borderRadius: BorderRadius.circular(6),
                                ),
                                child: Text(s,
                                    style: TextStyle(
                                        fontSize: 13,
                                        color: Colors.indigo.shade700)),
                              ))
                          .toList(),
                    ),
                  ],
                  if (vacancy.description != null &&
                      vacancy.description!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('Description',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(vacancy.description!,
                        style: const TextStyle(
                            fontSize: 14, height: 1.5, color: Color(0xFF333333))),
                  ],
                  if (vacancy.requirements != null &&
                      vacancy.requirements!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('Requirements',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(vacancy.requirements!,
                        style: const TextStyle(
                            fontSize: 14, height: 1.5, color: Color(0xFF333333))),
                  ],
                  // Contact info
                  if (vacancy.contactName != null ||
                      vacancy.contactEmail != null ||
                      vacancy.contactPhone != null) ...[
                    const SizedBox(height: 20),
                    const Text('Contact',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        color: const Color(0xFFF5F7FF),
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: const Color(0xFFE0E4FF)),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (vacancy.contactName != null)
                            _ContactRow(Icons.person_outline, vacancy.contactName!),
                          if (vacancy.contactEmail != null)
                            _ContactRow(Icons.email_outlined, vacancy.contactEmail!),
                          if (vacancy.contactPhone != null)
                            _ContactRow(Icons.phone_outlined, vacancy.contactPhone!),
                        ],
                      ),
                    ),
                  ],
                  // Action buttons
                  const SizedBox(height: 24),
                  if (_company != null)
                    OutlinedButton.icon(
                      onPressed: _viewCompany,
                      icon: const Icon(Icons.business_outlined, size: 18),
                      label: const Text('View company'),
                      style: OutlinedButton.styleFrom(
                        foregroundColor: Colors.indigo,
                        side: const BorderSide(color: Colors.indigo),
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(8)),
                        minimumSize: const Size(double.infinity, 44),
                      ),
                    ),
                  if (_isApplicant) ...[
                    const SizedBox(height: 10),
                    _buildApplyButton(),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildApplyButton() {
    final app = _myApplication;
    if (app == null) {
      return FilledButton.icon(
        onPressed: _isApplying ? null : _apply,
        icon: _isApplying
            ? const SizedBox(width: 16, height: 16,
                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
            : const Icon(Icons.send_outlined, size: 18),
        label: const Text('Apply'),
        style: FilledButton.styleFrom(
          backgroundColor: Colors.indigo,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          minimumSize: const Size(double.infinity, 44),
        ),
      );
    }
    final statusColor = switch (app.status) {
      ApplicationStatus.ACCEPTED => Colors.green,
      ApplicationStatus.REJECTED => Colors.red,
      ApplicationStatus.WITHDRAWN => Colors.grey,
      _ => Colors.indigo,
    };
    final canWithdraw = app.status.isActive;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(
            color: statusColor.withAlpha(20),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: statusColor.withAlpha(80)),
          ),
          child: Row(
            children: [
              Icon(Icons.info_outline, size: 16, color: statusColor),
              const SizedBox(width: 8),
              Text('Application status: ${app.status.displayName}',
                  style: TextStyle(color: statusColor, fontWeight: FontWeight.w500)),
            ],
          ),
        ),
        if (canWithdraw) ...[
          const SizedBox(height: 8),
          OutlinedButton.icon(
            onPressed: _isApplying ? null : _withdraw,
            icon: _isApplying
                ? const SizedBox(width: 16, height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2))
                : const Icon(Icons.close, size: 18),
            label: const Text('Withdraw application'),
            style: OutlinedButton.styleFrom(
              foregroundColor: Colors.red,
              side: const BorderSide(color: Colors.red),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              minimumSize: const Size(double.infinity, 44),
            ),
          ),
        ],
      ],
    );
  }

  String? get _salary {
    final v = widget.vacancy;
    return formatSalary(v.salaryFrom, v.salaryTo, v.currency);
  }
}

class _DetailChip extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color? color;

  const _DetailChip(this.icon, this.label, {this.color});

  @override
  Widget build(BuildContext context) {
    final c = color ?? const Color(0xFF555555);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: c.withAlpha(20),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 15, color: c),
          const SizedBox(width: 5),
          Text(label, style: TextStyle(fontSize: 13, color: c)),
        ],
      ),
    );
  }
}


// ── Resume section (compact, single resume in left panel for EMPLOYER) ────────

class _ResumeSection extends StatelessWidget {
  final Resume? resume;
  final bool isLoading;
  final VoidCallback onCreate;
  final VoidCallback? onView;
  final VoidCallback? onEdit;

  const _ResumeSection({
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
                    foregroundColor: Colors.indigo,
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
        color: const Color(0xFFF5F5F5),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(
            color: const Color(0xFFE0E0E0), style: BorderStyle.solid),
      ),
      child: Column(
        children: [
          Icon(Icons.description_outlined,
              size: 36, color: Colors.indigo.shade200),
          const SizedBox(height: 8),
          const Text('No resume yet',
              style: TextStyle(fontSize: 13, color: Color(0xFF666666))),
          const SizedBox(height: 10),
          FilledButton.icon(
            onPressed: onCreate,
            icon: const Icon(Icons.add, size: 16),
            label: const Text('Create resume'),
            style: FilledButton.styleFrom(
              backgroundColor: Colors.indigo,
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
      color: const Color(0xFFF8F8FF),
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
                  Container(
                    width: 36,
                    height: 36,
                    decoration: BoxDecoration(
                      color: Colors.indigo.shade100,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    alignment: Alignment.center,
                    child: Text(
                      resume.firstName.isNotEmpty
                          ? resume.firstName[0].toUpperCase()
                          : '?',
                      style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: Colors.indigo.shade700),
                    ),
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
                            style: const TextStyle(
                                fontSize: 12, color: Color(0xFF666666))),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.edit_outlined,
                        size: 16, color: Color(0xFF888888)),
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
                          color: Colors.indigo.shade50,
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(s,
                            style: TextStyle(
                                fontSize: 11,
                                color: Colors.indigo.shade700)),
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

// ── Resumes panel ─────────────────────────────────────────────────────────────

class _ResumesPanel extends StatelessWidget {
  final List<Resume> resumes;
  final bool isLoading;
  final VoidCallback onAdd;
  final void Function(Resume) onTap;
  final void Function(Resume) onEdit;

  const _ResumesPanel({
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
            itemBuilder: (_, i) => _ResumeCard(
              resume: resumes[i],
              onTap: () => onTap(resumes[i]),
              onEdit: () => onEdit(resumes[i]),
            ),
          ),
      ],
    );
  }
}

class _ResumeCard extends StatelessWidget {
  final Resume resume;
  final VoidCallback onTap;
  final VoidCallback onEdit;

  const _ResumeCard({
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
      color: Colors.white,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 44,
                height: 44,
                decoration: BoxDecoration(
                  color: Colors.indigo.shade50,
                  borderRadius: BorderRadius.circular(8),
                ),
                alignment: Alignment.center,
                child: Text(
                  resume.firstName.isNotEmpty
                      ? resume.firstName[0].toUpperCase()
                      : '?',
                  style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.indigo.shade700),
                ),
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
                      style: const TextStyle(
                          fontSize: 13, color: Color(0xFF666666)),
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
                            color: const Color(0xFFEEF2FF),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(s,
                              style: TextStyle(
                                  fontSize: 11,
                                  color: Colors.indigo.shade700)),
                        )).toList(),
                      ),
                    ],
                  ],
                ),
              ),
              IconButton(
                icon: const Icon(Icons.edit_outlined,
                    size: 18, color: Color(0xFF888888)),
                tooltip: 'Edit',
                onPressed: onEdit,
              ),
              const Icon(Icons.chevron_right,
                  size: 20, color: Color(0xFFCCCCCC)),
            ],
          ),
        ),
      ),
    );
  }
}

// ── Applicant tab panel (My Resumes | Companies) ───────────────────────────────

class _ApplicantTabPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final String? userRole;
  final List<Resume> resumes;
  final bool isResumesLoading;
  final VoidCallback onAddResume;
  final void Function(Resume) onTapResume;
  final void Function(Resume) onEditResume;

  const _ApplicantTabPanel({
    required this.vacancyApiService,
    this.userRole,
    required this.resumes,
    required this.isResumesLoading,
    required this.onAddResume,
    required this.onTapResume,
    required this.onEditResume,
  });

  @override
  State<_ApplicantTabPanel> createState() => _ApplicantTabPanelState();
}

class _ApplicantTabPanelState extends State<_ApplicantTabPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          color: Colors.white,
          child: TabBar(
            controller: _tabController,
            labelColor: Colors.indigo,
            unselectedLabelColor: const Color(0xFF888888),
            indicatorColor: Colors.indigo,
            tabs: const [
              Tab(icon: Icon(Icons.description_outlined, size: 18), text: 'My Resumes'),
              Tab(icon: Icon(Icons.business_outlined, size: 18), text: 'Companies'),
              Tab(icon: Icon(Icons.send_outlined, size: 18), text: 'My Applications'),
            ],
          ),
        ),
        const Divider(height: 1, color: Color(0xFFE0E0E0)),
        Expanded(
          child: TabBarView(
            controller: _tabController,
            children: [
              SingleChildScrollView(
                child: _ResumesPanel(
                  resumes: widget.resumes,
                  isLoading: widget.isResumesLoading,
                  onAdd: widget.onAddResume,
                  onTap: widget.onTapResume,
                  onEdit: widget.onEditResume,
                ),
              ),
              _CompanySearchPanel(vacancyApiService: widget.vacancyApiService),
              _MyApplicationsTab(vacancyApiService: widget.vacancyApiService),
            ],
          ),
        ),
      ],
    );
  }
}

// ── Company search panel ───────────────────────────────────────────────────────

class _CompanySearchPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;

  const _CompanySearchPanel({required this.vacancyApiService});

  @override
  State<_CompanySearchPanel> createState() => _CompanySearchPanelState();
}

class _CompanySearchPanelState extends State<_CompanySearchPanel> {
  final _nameCtrl = TextEditingController();
  final _cityCtrl = TextEditingController();
  bool _isLoading = false;
  List<Company> _companies = [];

  @override
  void initState() {
    super.initState();
    _search();
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _cityCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    setState(() => _isLoading = true);
    final results = await widget.vacancyApiService.searchCompanies(
      name: _nameCtrl.text.trim().isEmpty ? null : _nameCtrl.text.trim(),
      city: _cityCtrl.text.trim().isEmpty ? null : _cityCtrl.text.trim(),
    );
    if (mounted) {
      setState(() {
        _companies = results;
        _isLoading = false;
      });
    }
  }

  void _showDetail(Company c) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _CompanyDetailSheet(company: c),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          color: Colors.white,
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
          child: Column(
            children: [
              TextField(
                controller: _nameCtrl,
                decoration: InputDecoration(
                  hintText: 'Company name...',
                  prefixIcon:
                      const Icon(Icons.search, color: Color(0xFF888888)),
                  suffixIcon: _nameCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _nameCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: const Color(0xFFF5F5F5),
                ),
                onSubmitted: (_) => _search(),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _cityCtrl,
                decoration: InputDecoration(
                  hintText: 'City...',
                  prefixIcon: const Icon(Icons.location_on_outlined,
                      color: Color(0xFF888888)),
                  suffixIcon: _cityCtrl.text.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear, size: 18),
                          onPressed: () {
                            _cityCtrl.clear();
                            _search();
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
                  contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  filled: true,
                  fillColor: const Color(0xFFF5F5F5),
                ),
                onSubmitted: (_) => _search(),
              ),
            ],
          ),
        ),
        const Divider(height: 1, color: Color(0xFFE0E0E0)),
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _companies.isEmpty
                  ? EmptyState(
                      icon: Icons.business_outlined,
                      title: 'No companies found',
                      subtitle: 'Try different keywords or filters',
                      action: OutlinedButton(
                        onPressed: () {
                          _nameCtrl.clear();
                          _cityCtrl.clear();
                          _search();
                        },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.indigo,
                          side: const BorderSide(color: Colors.indigo),
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(8)),
                        ),
                        child: const Text('Reset filters'),
                      ),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.all(16),
                      itemCount: _companies.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (_, i) => _CompanyCard(
                        company: _companies[i],
                        onTap: () => _showDetail(_companies[i]),
                      ),
                    ),
        ),
      ],
    );
  }
}

class _CompanyCard extends StatelessWidget {
  final Company company;
  final VoidCallback onTap;

  const _CompanyCard({required this.company, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        borderRadius: BorderRadius.circular(10),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 44,
                height: 44,
                decoration: BoxDecoration(
                  color: Colors.indigo.shade50,
                  borderRadius: BorderRadius.circular(8),
                ),
                alignment: Alignment.center,
                child: Text(
                  company.name.isNotEmpty
                      ? company.name[0].toUpperCase()
                      : '?',
                  style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.indigo.shade600),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(company.name,
                        style: const TextStyle(
                            fontWeight: FontWeight.w600, fontSize: 15)),
                    if (company.industry != null)
                      Text(company.industry!,
                          style: const TextStyle(
                              fontSize: 13, color: Color(0xFF666666))),
                    const SizedBox(height: 4),
                    Wrap(
                      spacing: 12,
                      children: [
                        if (company.city != null)
                          _MetaTag(Icons.location_on_outlined, company.city!),
                        if (company.size != null)
                          _MetaTag(
                              Icons.people_outline, company.size!.displayName),
                        _MetaTag(Icons.star_outline,
                            company.rating.toStringAsFixed(1)),
                      ],
                    ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right,
                  size: 20, color: Color(0xFFCCCCCC)),
            ],
          ),
        ),
      ),
    );
  }
}

// ── Company detail sheet ───────────────────────────────────────────────────────

class _CompanyDetailSheet extends StatelessWidget {
  final Company company;

  const _CompanyDetailSheet({required this.company});

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.65,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, scrollCtrl) => Container(
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          children: [
            const BottomSheetHandle(),
            Expanded(
              child: ListView(
                controller: scrollCtrl,
                padding: const EdgeInsets.fromLTRB(24, 8, 24, 32),
                children: [
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: 56,
                        height: 56,
                        decoration: BoxDecoration(
                          color: Colors.indigo.shade50,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          company.name.isNotEmpty
                              ? company.name[0].toUpperCase()
                              : '?',
                          style: TextStyle(
                              fontSize: 26,
                              fontWeight: FontWeight.bold,
                              color: Colors.indigo.shade600),
                        ),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(company.name,
                                style: const TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold)),
                            if (company.industry != null)
                              Text(company.industry!,
                                  style: const TextStyle(
                                      fontSize: 14,
                                      color: Color(0xFF555555))),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Wrap(
                    spacing: 10,
                    runSpacing: 8,
                    children: [
                      if (company.city != null)
                        _DetailChip(Icons.location_on_outlined, company.city!),
                      if (company.country != null)
                        _DetailChip(Icons.flag_outlined, company.country!),
                      if (company.size != null)
                        _DetailChip(
                            Icons.people_outline, company.size!.displayName),
                      _DetailChip(
                        Icons.star_outline,
                        '${company.rating.toStringAsFixed(1)} (${company.reviewCount} reviews)',
                        color: const Color(0xFFCA8A04),
                      ),
                    ],
                  ),
                  if (company.website != null &&
                      company.website!.isNotEmpty) ...[
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        const Icon(Icons.link,
                            size: 16, color: Color(0xFF888888)),
                        const SizedBox(width: 6),
                        Text(company.website!,
                            style: const TextStyle(
                                fontSize: 14, color: Colors.indigo)),
                      ],
                    ),
                  ],
                  if (company.description != null &&
                      company.description!.isNotEmpty) ...[
                    const SizedBox(height: 20),
                    const Text('About',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 8),
                    Text(company.description!,
                        style: const TextStyle(
                            fontSize: 14,
                            height: 1.5,
                            color: Color(0xFF333333))),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ── Company owner panel ────────────────────────────────────────────────────────

class _CompanyOwnerPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _CompanyOwnerPanel({
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_CompanyOwnerPanel> createState() => _CompanyOwnerPanelState();
}

class _CompanyOwnerPanelState extends State<_CompanyOwnerPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;
  Company? _company;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _loadMyCompany();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadMyCompany() async {
    setState(() => _isLoading = true);
    final company = await widget.vacancyApiService.getMyCompany();
    if (mounted) {
      setState(() {
        _company = company;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    return Column(
      children: [
        Container(
          color: Colors.white,
          child: TabBar(
            controller: _tabController,
            labelColor: Colors.indigo,
            unselectedLabelColor: const Color(0xFF888888),
            indicatorColor: Colors.indigo,
            tabs: const [
              Tab(icon: Icon(Icons.business_outlined, size: 18), text: 'Company'),
              Tab(icon: Icon(Icons.work_outline, size: 18), text: 'Vacancies'),
              Tab(icon: Icon(Icons.people_outline, size: 18), text: 'Recruiters'),
              Tab(icon: Icon(Icons.inbox_outlined, size: 18), text: 'Applications'),
            ],
          ),
        ),
        const Divider(height: 1, color: Color(0xFFE0E0E0)),
        Expanded(
          child: TabBarView(
            controller: _tabController,
            children: [
              _CompanyTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                onCompanyChanged: (c) => setState(() => _company = c),
              ),
              _VacanciesTab(company: _company, vacancyApiService: widget.vacancyApiService),
              _RecruitersTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                userApiService: widget.userApiService,
              ),
              _RecruiterApplicationsTab(
                company: _company,
                vacancyApiService: widget.vacancyApiService,
                userApiService: widget.userApiService,
              ),
            ],
          ),
        ),
      ],
    );
  }
}

// ── Company tab ────────────────────────────────────────────────────────────────

class _CompanyTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final void Function(Company?) onCompanyChanged;

  const _CompanyTab({
    required this.company,
    required this.vacancyApiService,
    required this.onCompanyChanged,
  });

  @override
  State<_CompanyTab> createState() => _CompanyTabState();
}

class _CompanyTabState extends State<_CompanyTab> {
  bool _isEditing = false;

  @override
  Widget build(BuildContext context) {
    if (widget.company == null || _isEditing) {
      return _CompanyForm(
        company: widget.company,
        vacancyApiService: widget.vacancyApiService,
        onSaved: (c) {
          setState(() => _isEditing = false);
          widget.onCompanyChanged(c);
        },
        onCancel: widget.company != null ? () => setState(() => _isEditing = false) : null,
      );
    }
    return _CompanyInfo(
      company: widget.company!,
      onEdit: () => setState(() => _isEditing = true),
    );
  }
}

// ── Company info ───────────────────────────────────────────────────────────────

class _CompanyInfo extends StatelessWidget {
  final Company company;
  final VoidCallback onEdit;

  const _CompanyInfo({required this.company, required this.onEdit});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 56, height: 56,
                decoration: BoxDecoration(
                  color: Colors.indigo.shade50,
                  borderRadius: BorderRadius.circular(12),
                ),
                alignment: Alignment.center,
                child: Text(
                  company.name.isNotEmpty ? company.name[0].toUpperCase() : '?',
                  style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold,
                      color: Colors.indigo.shade600),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(company.name,
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  if (company.industry != null)
                    Text(company.industry!,
                        style: const TextStyle(fontSize: 14, color: Color(0xFF666666))),
                ]),
              ),
              OutlinedButton.icon(
                onPressed: onEdit,
                icon: const Icon(Icons.edit_outlined, size: 16),
                label: const Text('Edit'),
                style: OutlinedButton.styleFrom(
                  foregroundColor: Colors.indigo,
                  side: const BorderSide(color: Colors.indigo),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
          Wrap(
            spacing: 10, runSpacing: 8,
            children: [
              if (company.city != null)
                _DetailChip(Icons.location_on_outlined, company.city!),
              if (company.country != null)
                _DetailChip(Icons.flag_outlined, company.country!),
              if (company.size != null)
                _DetailChip(Icons.people_outline, company.size!.displayName),
              _DetailChip(Icons.star_outline,
                  '${company.rating.toStringAsFixed(1)} (${company.reviewCount} reviews)',
                  color: const Color(0xFFCA8A04)),
            ],
          ),
          if (company.website != null && company.website!.isNotEmpty) ...[
            const SizedBox(height: 16),
            Row(children: [
              const Icon(Icons.link, size: 16, color: Color(0xFF888888)),
              const SizedBox(width: 6),
              Text(company.website!,
                  style: const TextStyle(fontSize: 14, color: Colors.indigo)),
            ]),
          ],
          if (company.description != null && company.description!.isNotEmpty) ...[
            const SizedBox(height: 20),
            const Text('About', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
            const SizedBox(height: 8),
            Text(company.description!,
                style: const TextStyle(fontSize: 14, height: 1.5, color: Color(0xFF333333))),
          ],
        ],
      ),
    );
  }
}

// ── Company form ───────────────────────────────────────────────────────────────

class _CompanyForm extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final void Function(Company) onSaved;
  final VoidCallback? onCancel;

  const _CompanyForm({
    required this.company,
    required this.vacancyApiService,
    required this.onSaved,
    this.onCancel,
  });

  @override
  State<_CompanyForm> createState() => _CompanyFormState();
}

class _CompanyFormState extends State<_CompanyForm> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameCtrl;
  late final TextEditingController _descCtrl;
  late final TextEditingController _websiteCtrl;
  late final TextEditingController _industryCtrl;
  late final TextEditingController _cityCtrl;
  late final TextEditingController _countryCtrl;
  CompanySize? _size;
  bool _isSaving = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    final c = widget.company;
    _nameCtrl = TextEditingController(text: c?.name ?? '');
    _descCtrl = TextEditingController(text: c?.description ?? '');
    _websiteCtrl = TextEditingController(text: c?.website ?? '');
    _industryCtrl = TextEditingController(text: c?.industry ?? '');
    _cityCtrl = TextEditingController(text: c?.city ?? '');
    _countryCtrl = TextEditingController(text: c?.country ?? '');
    _size = c?.size;
  }

  @override
  void dispose() {
    for (final c in [_nameCtrl, _descCtrl, _websiteCtrl, _industryCtrl, _cityCtrl, _countryCtrl]) {
      c.dispose();
    }
    super.dispose();
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _isSaving = true; _error = null; });

    final body = <String, dynamic>{
      'name': _nameCtrl.text.trim(),
      if (_descCtrl.text.trim().isNotEmpty) 'description': _descCtrl.text.trim(),
      if (_websiteCtrl.text.trim().isNotEmpty) 'website': _websiteCtrl.text.trim(),
      if (_industryCtrl.text.trim().isNotEmpty) 'industry': _industryCtrl.text.trim(),
      if (_size != null) 'size': _size!.name,
      if (_cityCtrl.text.trim().isNotEmpty) 'city': _cityCtrl.text.trim(),
      if (_countryCtrl.text.trim().isNotEmpty) 'country': _countryCtrl.text.trim(),
    };

    final result = widget.company == null
        ? await widget.vacancyApiService.createCompany(body)
        : await widget.vacancyApiService.updateCompany(widget.company!.id, body);

    if (mounted) {
      setState(() => _isSaving = false);
      if (result != null) {
        widget.onSaved(result);
      } else {
        setState(() => _error = 'Failed to save. Please try again.');
      }
    }
  }

  InputDecoration _dec(String label) => InputDecoration(
        labelText: label,
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
      );

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Form(
        key: _formKey,
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(widget.company == null ? 'Create company' : 'Edit company',
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 20),
          if (_error != null) ...[
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.red.shade50, borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.red.shade200),
              ),
              child: Text(_error!, style: TextStyle(color: Colors.red.shade700, fontSize: 13)),
            ),
            const SizedBox(height: 16),
          ],
          TextFormField(controller: _nameCtrl, decoration: _dec('Company name *'),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Required' : null),
          const SizedBox(height: 12),
          TextFormField(controller: _industryCtrl, decoration: _dec('Industry')),
          const SizedBox(height: 12),
          DropdownButtonFormField<CompanySize?>(
            value: _size, decoration: _dec('Company size'),
            items: [
              const DropdownMenuItem(value: null, child: Text('Not specified')),
              ...CompanySize.values.map((s) => DropdownMenuItem(value: s, child: Text(s.displayName))),
            ],
            onChanged: (v) => setState(() => _size = v),
          ),
          const SizedBox(height: 12),
          TextFormField(controller: _cityCtrl, decoration: _dec('City')),
          const SizedBox(height: 12),
          TextFormField(controller: _countryCtrl, decoration: _dec('Country')),
          const SizedBox(height: 12),
          TextFormField(controller: _websiteCtrl, decoration: _dec('Website')),
          const SizedBox(height: 12),
          TextFormField(controller: _descCtrl, decoration: _dec('Description'), maxLines: 4, minLines: 2),
          const SizedBox(height: 24),
          Row(children: [
            if (widget.onCancel != null) ...[
              Expanded(
                child: OutlinedButton(
                  onPressed: widget.onCancel,
                  style: OutlinedButton.styleFrom(
                    foregroundColor: const Color(0xFF555555),
                    side: const BorderSide(color: Color(0xFFCCCCCC)),
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                  child: const Text('Cancel'),
                ),
              ),
              const SizedBox(width: 12),
            ],
            Expanded(
              child: FilledButton(
                onPressed: _isSaving ? null : _save,
                style: FilledButton.styleFrom(
                  backgroundColor: Colors.indigo,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                ),
                child: _isSaving
                    ? const SizedBox(width: 18, height: 18,
                        child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                    : Text(widget.company == null ? 'Create' : 'Save'),
              ),
            ),
          ]),
        ]),
      ),
    );
  }
}

// ── Vacancies tab ──────────────────────────────────────────────────────────────

class _VacanciesTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;

  const _VacanciesTab({required this.company, required this.vacancyApiService});

  @override
  State<_VacanciesTab> createState() => _VacanciesTabState();
}

class _VacanciesTabState extends State<_VacanciesTab> {
  List<Vacancy> _vacancies = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.company != null) _load();
  }

  Future<void> _load() async {
    setState(() => _isLoading = true);
    final active = await widget.vacancyApiService.searchVacancies(
        companyId: widget.company!.id, status: VacancyStatus.ACTIVE);
    final draft = await widget.vacancyApiService.searchVacancies(
        companyId: widget.company!.id, status: VacancyStatus.DRAFT);
    if (mounted) setState(() { _vacancies = [...active, ...draft]; _isLoading = false; });
  }

  Future<void> _showCreate() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => _VacancyFormDialog(
          companyId: widget.company!.id, vacancyApiService: widget.vacancyApiService),
    );
    if (ok == true) _load();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.company == null) {
      return const Center(child: Padding(padding: EdgeInsets.all(32),
          child: Text('Create your company first to manage vacancies.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Color(0xFF888888)))));
    }
    return Column(children: [
      Container(
        color: Colors.white,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          const Text('Vacancies', style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
          FilledButton.icon(
            onPressed: _showCreate,
            icon: const Icon(Icons.add, size: 16),
            label: const Text('New vacancy'),
            style: FilledButton.styleFrom(backgroundColor: Colors.indigo,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          ),
        ]),
      ),
      const Divider(height: 1, color: Color(0xFFE0E0E0)),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _vacancies.isEmpty
                ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(Icons.work_off_outlined, size: 48, color: Colors.indigo.shade200),
                    const SizedBox(height: 12),
                    const Text('No vacancies yet',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600,
                            color: Color(0xFF444444))),
                  ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _vacancies.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (_, i) => _VacancyBrowseCard(
                      vacancy: _vacancies[i],
                      onTap: () => showModalBottomSheet(
                        context: context, isScrollControlled: true,
                        backgroundColor: Colors.transparent,
                        builder: (_) => _VacancyDetailSheet(
                          vacancy: _vacancies[i],
                          vacancyApiService: widget.vacancyApiService,
                        ),
                      ),
                    ),
                  ),
      ),
    ]);
  }
}

// ── Vacancy form dialog ────────────────────────────────────────────────────────

class _VacancyFormDialog extends StatefulWidget {
  final String companyId;
  final VacancyApiService vacancyApiService;

  const _VacancyFormDialog({required this.companyId, required this.vacancyApiService});

  @override
  State<_VacancyFormDialog> createState() => _VacancyFormDialogState();
}

class _VacancyFormDialogState extends State<_VacancyFormDialog> {
  final _formKey = GlobalKey<FormState>();
  final _titleCtrl = TextEditingController();
  final _descCtrl = TextEditingController();
  final _reqCtrl = TextEditingController();
  final _salaryFromCtrl = TextEditingController();
  final _salaryToCtrl = TextEditingController();
  final _currencyCtrl = TextEditingController(text: 'RUB');
  final _cityCtrl = TextEditingController();
  final _countryCtrl = TextEditingController();
  final _skillsCtrl = TextEditingController();
  final _contactNameCtrl = TextEditingController();
  final _contactEmailCtrl = TextEditingController();
  final _contactPhoneCtrl = TextEditingController();
  EmploymentType? _employmentType;
  WorkFormat? _workFormat;
  ExperienceLevel? _experienceLevel;
  bool _isSaving = false;

  @override
  void dispose() {
    for (final c in [_titleCtrl, _descCtrl, _reqCtrl, _salaryFromCtrl,
        _salaryToCtrl, _currencyCtrl, _cityCtrl, _countryCtrl, _skillsCtrl,
        _contactNameCtrl, _contactEmailCtrl, _contactPhoneCtrl]) {
      c.dispose();
    }
    super.dispose();
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isSaving = true);
    final skills = _skillsCtrl.text
        .split(',').map((s) => s.trim()).where((s) => s.isNotEmpty).toList();
    final body = <String, dynamic>{
      'companyId': widget.companyId,
      'title': _titleCtrl.text.trim(),
      if (_descCtrl.text.trim().isNotEmpty) 'description': _descCtrl.text.trim(),
      if (_reqCtrl.text.trim().isNotEmpty) 'requirements': _reqCtrl.text.trim(),
      if (_salaryFromCtrl.text.trim().isNotEmpty)
        'salaryFrom': int.tryParse(_salaryFromCtrl.text.trim()),
      if (_salaryToCtrl.text.trim().isNotEmpty)
        'salaryTo': int.tryParse(_salaryToCtrl.text.trim()),
      if (_currencyCtrl.text.trim().isNotEmpty) 'currency': _currencyCtrl.text.trim(),
      if (_employmentType != null) 'employmentType': _employmentType!.name,
      if (_workFormat != null) 'workFormat': _workFormat!.name,
      if (_experienceLevel != null) 'experienceLevel': _experienceLevel!.name,
      if (_cityCtrl.text.trim().isNotEmpty) 'city': _cityCtrl.text.trim(),
      if (_countryCtrl.text.trim().isNotEmpty) 'country': _countryCtrl.text.trim(),
      'skills': skills,
      'status': 'ACTIVE',
      if (_contactNameCtrl.text.trim().isNotEmpty) 'contactName': _contactNameCtrl.text.trim(),
      if (_contactEmailCtrl.text.trim().isNotEmpty) 'contactEmail': _contactEmailCtrl.text.trim(),
      if (_contactPhoneCtrl.text.trim().isNotEmpty) 'contactPhone': _contactPhoneCtrl.text.trim(),
    };
    final result = await widget.vacancyApiService.createVacancy(body);
    if (mounted) {
      setState(() => _isSaving = false);
      Navigator.of(context).pop(result != null);
    }
  }

  InputDecoration _dec(String label) => InputDecoration(
        labelText: label,
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
        isDense: true,
      );

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      title: const Text('New vacancy'),
      content: SizedBox(
        width: 520,
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(child: Column(mainAxisSize: MainAxisSize.min, children: [
            TextFormField(controller: _titleCtrl, decoration: _dec('Title *'),
                validator: (v) => (v == null || v.trim().isEmpty) ? 'Required' : null),
            const SizedBox(height: 10),
            Row(children: [
              Expanded(child: DropdownButtonFormField<EmploymentType?>(
                value: _employmentType, decoration: _dec('Employment type'),
                items: [const DropdownMenuItem(value: null, child: Text('Any')),
                  ...EmploymentType.values.map((e) => DropdownMenuItem(value: e, child: Text(e.displayName)))],
                onChanged: (v) => setState(() => _employmentType = v),
              )),
              const SizedBox(width: 10),
              Expanded(child: DropdownButtonFormField<WorkFormat?>(
                value: _workFormat, decoration: _dec('Work format'),
                items: [const DropdownMenuItem(value: null, child: Text('Any')),
                  ...WorkFormat.values.map((e) => DropdownMenuItem(value: e, child: Text(e.displayName)))],
                onChanged: (v) => setState(() => _workFormat = v),
              )),
            ]),
            const SizedBox(height: 10),
            DropdownButtonFormField<ExperienceLevel?>(
              value: _experienceLevel, decoration: _dec('Experience level'),
              items: [const DropdownMenuItem(value: null, child: Text('Any')),
                ...ExperienceLevel.values.map((e) => DropdownMenuItem(value: e, child: Text(e.displayName)))],
              onChanged: (v) => setState(() => _experienceLevel = v),
            ),
            const SizedBox(height: 10),
            Row(children: [
              Expanded(child: TextFormField(controller: _salaryFromCtrl,
                  decoration: _dec('Salary from'), keyboardType: TextInputType.number)),
              const SizedBox(width: 10),
              Expanded(child: TextFormField(controller: _salaryToCtrl,
                  decoration: _dec('Salary to'), keyboardType: TextInputType.number)),
              const SizedBox(width: 10),
              SizedBox(width: 80, child: TextFormField(controller: _currencyCtrl, decoration: _dec('Currency'))),
            ]),
            const SizedBox(height: 10),
            Row(children: [
              Expanded(child: TextFormField(controller: _cityCtrl, decoration: _dec('City'))),
              const SizedBox(width: 10),
              Expanded(child: TextFormField(controller: _countryCtrl, decoration: _dec('Country'))),
            ]),
            const SizedBox(height: 10),
            TextFormField(controller: _skillsCtrl, decoration: _dec('Skills (comma-separated)')),
            const SizedBox(height: 10),
            TextFormField(controller: _descCtrl, decoration: _dec('Description'), maxLines: 3, minLines: 2),
            const SizedBox(height: 10),
            TextFormField(controller: _reqCtrl, decoration: _dec('Requirements'), maxLines: 3, minLines: 2),
            const SizedBox(height: 14),
            const Divider(),
            const SizedBox(height: 4),
            const Align(alignment: Alignment.centerLeft,
                child: Text('Contact information', style: TextStyle(fontWeight: FontWeight.w600, fontSize: 13))),
            const SizedBox(height: 10),
            TextFormField(controller: _contactNameCtrl, decoration: _dec('Contact name')),
            const SizedBox(height: 10),
            TextFormField(controller: _contactEmailCtrl, decoration: _dec('Contact email'),
                keyboardType: TextInputType.emailAddress),
            const SizedBox(height: 10),
            TextFormField(controller: _contactPhoneCtrl, decoration: _dec('Contact phone'),
                keyboardType: TextInputType.phone),
          ])),
        ),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(context).pop(false), child: const Text('Cancel')),
        FilledButton(
          onPressed: _isSaving ? null : _save,
          style: FilledButton.styleFrom(backgroundColor: Colors.indigo),
          child: _isSaving
              ? const SizedBox(width: 16, height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
              : const Text('Create'),
        ),
      ],
    );
  }
}

// ── Recruiters tab ─────────────────────────────────────────────────────────────

class _RecruitersTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _RecruitersTab({
    required this.company,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_RecruitersTab> createState() => _RecruitersTabState();
}

class _RecruitersTabState extends State<_RecruitersTab> {
  List<_RecruiterEntry> _recruiters = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.company != null) _load();
  }

  Future<void> _load() async {
    if (widget.company == null) return;
    setState(() => _isLoading = true);
    final raw = await widget.vacancyApiService.getRecruiters(widget.company!.id);
    final entries = await Future.wait(raw.map((r) async {
      final user = await widget.userApiService.getUserById(r.accountId);
      return _RecruiterEntry(recruiter: r, user: user);
    }));
    if (mounted) setState(() { _recruiters = entries; _isLoading = false; });
  }

  Future<void> _showAdd() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => _AddRecruiterDialog(
        companyId: widget.company!.id,
        vacancyApiService: widget.vacancyApiService,
        userApiService: widget.userApiService,
      ),
    );
    if (ok == true) _load();
  }

  Future<void> _remove(CompanyRecruiter r) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        title: const Text('Remove recruiter?'),
        content: const Text('This will revoke their recruiter access.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancel')),
          FilledButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: FilledButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Remove'),
          ),
        ],
      ),
    );
    if (ok == true) {
      await widget.vacancyApiService.removeRecruiter(widget.company!.id, r.accountId);
      _load();
    }
  }

  @override
  Widget build(BuildContext context) {
    if (widget.company == null) {
      return const Center(child: Padding(padding: EdgeInsets.all(32),
          child: Text('Create your company first to manage recruiters.',
              textAlign: TextAlign.center, style: TextStyle(color: Color(0xFF888888)))));
    }
    return Column(children: [
      Container(
        color: Colors.white,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          const Text('Recruiters', style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
          FilledButton.icon(
            onPressed: _showAdd,
            icon: const Icon(Icons.person_add_outlined, size: 16),
            label: const Text('Add recruiter'),
            style: FilledButton.styleFrom(backgroundColor: Colors.indigo,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          ),
        ]),
      ),
      const Divider(height: 1, color: Color(0xFFE0E0E0)),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _recruiters.isEmpty
                ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(Icons.people_outline, size: 48, color: Colors.indigo.shade200),
                    const SizedBox(height: 12),
                    const Text('No recruiters yet',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600,
                            color: Color(0xFF444444))),
                  ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _recruiters.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (_, i) {
                      final e = _recruiters[i];
                      final u = e.user;
                      return Material(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(10),
                        child: Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                          child: Row(children: [
                            CircleAvatar(
                              radius: 20, backgroundColor: Colors.indigo.shade50,
                              child: Text(
                                u != null && u.firstName.isNotEmpty
                                    ? u.firstName[0].toUpperCase() : '?',
                                style: TextStyle(fontWeight: FontWeight.bold,
                                    color: Colors.indigo.shade600),
                              ),
                            ),
                            const SizedBox(width: 12),
                            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                              Text(u != null ? u.fullName : e.recruiter.accountId,
                                  style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                              if (u != null)
                                Text('@${u.login}',
                                    style: const TextStyle(fontSize: 12, color: Color(0xFF888888))),
                            ])),
                            IconButton(
                              icon: const Icon(Icons.person_remove_outlined,
                                  size: 20, color: Color(0xFFCC4444)),
                              tooltip: 'Remove',
                              onPressed: () => _remove(e.recruiter),
                            ),
                          ]),
                        ),
                      );
                    },
                  ),
      ),
    ]);
  }
}

class _RecruiterEntry {
  final CompanyRecruiter recruiter;
  final UserShort? user;
  _RecruiterEntry({required this.recruiter, required this.user});
}

// ── Add recruiter dialog ───────────────────────────────────────────────────────

class _AddRecruiterDialog extends StatefulWidget {
  final String companyId;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _AddRecruiterDialog({
    required this.companyId,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_AddRecruiterDialog> createState() => _AddRecruiterDialogState();
}

class _AddRecruiterDialogState extends State<_AddRecruiterDialog> {
  final _searchCtrl = TextEditingController();
  List<UserShort> _results = [];
  UserShort? _selected;
  bool _isSearching = false;
  bool _isAdding = false;
  String? _error;

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    final q = _searchCtrl.text.trim();
    if (q.isEmpty) return;
    setState(() { _isSearching = true; _selected = null; _error = null; });
    final results = await widget.userApiService.searchUsers(q);
    if (mounted) setState(() { _results = results; _isSearching = false; });
  }

  Future<void> _add() async {
    if (_selected == null) return;
    setState(() { _isAdding = true; _error = null; });
    final ok = await widget.vacancyApiService.addRecruiter(widget.companyId, _selected!.id);
    if (mounted) {
      setState(() => _isAdding = false);
      if (ok) {
        Navigator.of(context).pop(true);
      } else {
        setState(() => _error =
            'Failed to add recruiter. They may already be assigned to a company.');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      title: const Text('Add recruiter'),
      content: SizedBox(
        width: 420,
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          if (_error != null) ...[
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(color: Colors.red.shade50,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.red.shade200)),
              child: Text(_error!, style: TextStyle(color: Colors.red.shade700, fontSize: 13)),
            ),
            const SizedBox(height: 12),
          ],
          TextField(
            controller: _searchCtrl,
            decoration: InputDecoration(
              hintText: 'Search by name or login...',
              prefixIcon: const Icon(Icons.search, color: Color(0xFF888888)),
              suffixIcon: IconButton(icon: const Icon(Icons.search), onPressed: _search),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
              contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
            ),
            onSubmitted: (_) => _search(),
          ),
          const SizedBox(height: 12),
          if (_isSearching)
            const Padding(padding: EdgeInsets.all(16), child: CircularProgressIndicator())
          else if (_results.isEmpty && _searchCtrl.text.isNotEmpty)
            const Padding(padding: EdgeInsets.all(16),
                child: Text('No users found', style: TextStyle(color: Color(0xFF888888))))
          else
            ConstrainedBox(
              constraints: const BoxConstraints(maxHeight: 240),
              child: ListView.builder(
                shrinkWrap: true,
                itemCount: _results.length,
                itemBuilder: (_, i) {
                  final u = _results[i];
                  final sel = _selected?.id == u.id;
                  return ListTile(
                    dense: true,
                    leading: CircleAvatar(
                      radius: 16, backgroundColor: Colors.indigo.shade50,
                      child: Text(
                        u.firstName.isNotEmpty ? u.firstName[0].toUpperCase() : '?',
                        style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold,
                            color: Colors.indigo.shade600),
                      ),
                    ),
                    title: Text(u.fullName, style: const TextStyle(fontSize: 14)),
                    subtitle: Text('@${u.login}', style: const TextStyle(fontSize: 12)),
                    selected: sel,
                    selectedTileColor: Colors.indigo.shade50,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    onTap: () => setState(() => _selected = u),
                    trailing: sel ? Icon(Icons.check_circle, color: Colors.indigo.shade400) : null,
                  );
                },
              ),
            ),
        ]),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(context).pop(false), child: const Text('Cancel')),
        FilledButton(
          onPressed: (_selected == null || _isAdding) ? null : _add,
          style: FilledButton.styleFrom(backgroundColor: Colors.indigo),
          child: _isAdding
              ? const SizedBox(width: 16, height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
              : const Text('Add'),
        ),
      ],
    );
  }
}

// ── Contact row ────────────────────────────────────────────────────────────────

class _ContactRow extends StatelessWidget {
  final IconData icon;
  final String text;
  const _ContactRow(this.icon, this.text);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 6),
      child: Row(children: [
        Icon(icon, size: 16, color: Colors.indigo.shade400),
        const SizedBox(width: 8),
        Expanded(child: Text(text,
            style: const TextStyle(fontSize: 14, color: Color(0xFF333333)))),
      ]),
    );
  }
}

// ── My Applications tab (APPLICANT) ───────────────────────────────────────────

class _MyApplicationsTab extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  const _MyApplicationsTab({required this.vacancyApiService});

  @override
  State<_MyApplicationsTab> createState() => _MyApplicationsTabState();
}

class _MyApplicationsTabState extends State<_MyApplicationsTab> {
  List<Application> _applications = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _isLoading = true);
    final apps = await widget.vacancyApiService.getMyApplications();
    if (mounted) setState(() { _applications = apps; _isLoading = false; });
  }

  Future<void> _withdraw(Application app) async {
    final ok = await widget.vacancyApiService.withdrawApplication(app.vacancyId);
    if (ok && mounted) _load();
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    if (_applications.isEmpty) {
      return const EmptyState(
        icon: Icons.send_outlined,
        title: 'No applications yet',
        subtitle: 'Browse vacancies and apply to get started',
      );
    }
    return RefreshIndicator(
      onRefresh: _load,
      child: ListView.separated(
        padding: const EdgeInsets.all(16),
        itemCount: _applications.length,
        separatorBuilder: (_, __) => const SizedBox(height: 8),
        itemBuilder: (_, i) {
          final app = _applications[i];
          final color = StatusBadge.colorFor(app.status);
          return Material(
            color: Colors.white,
            borderRadius: BorderRadius.circular(10),
            child: Padding(
              padding: const EdgeInsets.all(14),
              child: Row(children: [
                Container(
                  width: 40, height: 40,
                  decoration: BoxDecoration(color: color.withAlpha(30),
                      borderRadius: BorderRadius.circular(8)),
                  child: Icon(Icons.work_outline, color: color, size: 20),
                ),
                const SizedBox(width: 12),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text('Vacancy ${app.vacancyId.substring(0, 8)}…',
                      style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                  const SizedBox(height: 4),
                  StatusBadge(app.status),
                ])),
                if (app.status.isActive)
                  IconButton(
                    icon: const Icon(Icons.close, color: Colors.red, size: 20),
                    tooltip: 'Withdraw',
                    onPressed: () => _withdraw(app),
                  ),
              ]),
            ),
          );
        },
      ),
    );
  }
}

// ── Recruiter panel ────────────────────────────────────────────────────────────

class _RecruiterPanel extends StatefulWidget {
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _RecruiterPanel({
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_RecruiterPanel> createState() => _RecruiterPanelState();
}

class _RecruiterPanelState extends State<_RecruiterPanel>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;
  Company? _company;
  bool _isLoadingCompany = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _loadCompany();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadCompany() async {
    final company = await widget.vacancyApiService.getMyCompany();
    if (mounted) setState(() { _company = company; _isLoadingCompany = false; });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingCompany) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_company == null) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(32),
          child: Text('You are not assigned to any company yet.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Color(0xFF888888))),
        ),
      );
    }
    return Column(children: [
      Container(
        color: Colors.white,
        child: TabBar(
          controller: _tabController,
          labelColor: Colors.indigo,
          unselectedLabelColor: const Color(0xFF888888),
          indicatorColor: Colors.indigo,
          tabs: const [
            Tab(icon: Icon(Icons.work_outline, size: 18), text: 'Vacancies'),
            Tab(icon: Icon(Icons.inbox_outlined, size: 18), text: 'Applications'),
            Tab(icon: Icon(Icons.person_search_outlined, size: 18), text: 'Applicants'),
          ],
        ),
      ),
      const Divider(height: 1, color: Color(0xFFE0E0E0)),
      Expanded(
        child: TabBarView(
          controller: _tabController,
          children: [
            _VacanciesTab(
              company: _company,
              vacancyApiService: widget.vacancyApiService,
            ),
            _RecruiterApplicationsTab(
              company: _company!,
              vacancyApiService: widget.vacancyApiService,
              userApiService: widget.userApiService,
            ),
            _ApplicantSearchTab(
              userApiService: widget.userApiService,
            ),
          ],
        ),
      ),
    ]);
  }
}

// ── Recruiter Applications tab ─────────────────────────────────────────────────

class _RecruiterApplicationsTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _RecruiterApplicationsTab({
    required this.company,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_RecruiterApplicationsTab> createState() =>
      _RecruiterApplicationsTabState();
}

class _RecruiterApplicationsTabState extends State<_RecruiterApplicationsTab> {
  List<Vacancy> _vacancies = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final companyId = widget.company?.id;
    if (companyId == null) return;
    setState(() => _isLoading = true);
    final active = await widget.vacancyApiService.searchVacancies(
        companyId: companyId, status: VacancyStatus.ACTIVE);
    final draft = await widget.vacancyApiService.searchVacancies(
        companyId: companyId, status: VacancyStatus.DRAFT);
    if (mounted) setState(() { _vacancies = [...active, ...draft]; _isLoading = false; });
  }

  void _showApplications(Vacancy vacancy) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _VacancyApplicationsSheet(
        vacancy: vacancy,
        vacancyApiService: widget.vacancyApiService,
        userApiService: widget.userApiService,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    if (_vacancies.isEmpty) {
      return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(Icons.inbox_outlined, size: 48, color: Colors.indigo.shade200),
        const SizedBox(height: 12),
        const Text('No vacancies yet',
            style: TextStyle(fontSize: 15, color: Color(0xFF444444))),
      ]));
    }
    return ListView.separated(
      padding: const EdgeInsets.all(16),
      itemCount: _vacancies.length,
      separatorBuilder: (_, __) => const SizedBox(height: 8),
      itemBuilder: (_, i) {
        final v = _vacancies[i];
        return Material(
          color: Colors.white,
          borderRadius: BorderRadius.circular(10),
          child: ListTile(
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            title: Text(v.title,
                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
            subtitle: Text(v.status.displayName,
                style: TextStyle(fontSize: 12,
                    color: v.status == VacancyStatus.ACTIVE ? Colors.green : Colors.grey)),
            trailing: FilledButton.icon(
              onPressed: () => _showApplications(v),
              icon: const Icon(Icons.people_outline, size: 16),
              label: const Text('Applications'),
              style: FilledButton.styleFrom(
                backgroundColor: Colors.indigo,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ),
        );
      },
    );
  }
}

// ── Vacancy Applications Sheet (Recruiter / Company) ──────────────────────────

class _VacancyApplicationsSheet extends StatefulWidget {
  final Vacancy vacancy;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const _VacancyApplicationsSheet({
    required this.vacancy,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<_VacancyApplicationsSheet> createState() =>
      _VacancyApplicationsSheetState();
}

class _VacancyApplicationsSheetState
    extends State<_VacancyApplicationsSheet> {
  List<({Application app, UserShort? user})> _entries = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _isLoading = true);
    final apps =
        await widget.vacancyApiService.getVacancyApplications(widget.vacancy.id);
    final entries = await Future.wait(apps.map((a) async {
      final user = await widget.userApiService.getUserById(a.applicantAccountId);
      return (app: a, user: user);
    }));
    if (mounted) setState(() { _entries = entries; _isLoading = false; });
  }

  Future<void> _changeStatus(
      Application app, ApplicationStatus newStatus) async {
    await widget.vacancyApiService
        .updateApplicationStatus(widget.vacancy.id, app.id, newStatus);
    _load();
  }


  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.75,
      minChildSize: 0.4,
      maxChildSize: 0.95,
      builder: (_, ctrl) => Container(
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(children: [
          const BottomSheetHandle(),
          Padding(
            padding: const EdgeInsets.fromLTRB(24, 8, 24, 12),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('Applications',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              Text(widget.vacancy.title,
                  style: const TextStyle(fontSize: 13, color: Color(0xFF666666))),
            ]),
          ),
          const Divider(height: 1),
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _entries.isEmpty
                    ? const EmptyState(
                        icon: Icons.people_outline,
                        title: 'No applications yet',
                      )
                    : ListView.separated(
                        controller: ctrl,
                        padding: const EdgeInsets.all(16),
                        itemCount: _entries.length,
                        separatorBuilder: (_, __) => const SizedBox(height: 8),
                        itemBuilder: (_, i) {
                          final entry = _entries[i];
                          final app = entry.app;
                          final user = entry.user;
                          final canAct = app.status != ApplicationStatus.WITHDRAWN &&
                              app.status != ApplicationStatus.REJECTED &&
                              app.status != ApplicationStatus.ACCEPTED;
                          return Material(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(10),
                            elevation: 0.5,
                            child: Padding(
                              padding: const EdgeInsets.all(12),
                              child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                Row(children: [
                                  InitialsAvatar(
                                    name: user?.firstName ?? '?',
                                    color: Colors.indigo.shade100,
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                      child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                        Text(user?.fullName ?? 'Unknown',
                                            style: const TextStyle(
                                                fontWeight: FontWeight.w600,
                                                fontSize: 14)),
                                        Text('@${user?.login ?? '…'}',
                                            style: const TextStyle(
                                                fontSize: 12,
                                                color: Color(0xFF666666))),
                                      ])),
                                  StatusBadge(app.status),
                                ]),
                                if (canAct) ...[
                                  const SizedBox(height: 10),
                                  Row(children: [
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.REVIEWED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: Colors.orange,
                                        side: const BorderSide(
                                            color: Colors.orange),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Reviewed',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                    const SizedBox(width: 8),
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.ACCEPTED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: Colors.green,
                                        side: const BorderSide(
                                            color: Colors.green),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Accept',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                    const SizedBox(width: 8),
                                    Expanded(child: OutlinedButton(
                                      onPressed: () => _changeStatus(
                                          app, ApplicationStatus.REJECTED),
                                      style: OutlinedButton.styleFrom(
                                        foregroundColor: Colors.red,
                                        side: const BorderSide(color: Colors.red),
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 6),
                                      ),
                                      child: const Text('Reject',
                                          style: TextStyle(fontSize: 12)),
                                    )),
                                  ]),
                                ],
                              ]),
                            ),
                          );
                        },
                      ),
          ),
        ]),
      ),
    );
  }
}

// ── Applicant search tab (Recruiter) ──────────────────────────────────────────

class _ApplicantSearchTab extends StatefulWidget {
  final UserApiService userApiService;

  const _ApplicantSearchTab({required this.userApiService});

  @override
  State<_ApplicantSearchTab> createState() => _ApplicantSearchTabState();
}

class _ApplicantSearchTabState extends State<_ApplicantSearchTab> {
  final _searchCtrl = TextEditingController();
  List<UserShort> _results = [];
  bool _isLoading = false;

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    final q = _searchCtrl.text.trim();
    if (q.isEmpty) return;
    setState(() => _isLoading = true);
    final results = await widget.userApiService.searchUsers(q);
    if (mounted) setState(() { _results = results; _isLoading = false; });
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Container(
        color: Colors.white,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: TextField(
          controller: _searchCtrl,
          decoration: InputDecoration(
            hintText: 'Search by name or login...',
            prefixIcon: const Icon(Icons.search, color: Color(0xFF888888)),
            suffixIcon: _searchCtrl.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear, size: 18),
                    onPressed: () {
                      _searchCtrl.clear();
                      setState(() => _results = []);
                    })
                : null,
            border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10),
                borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
            enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10),
                borderSide: const BorderSide(color: Color(0xFFE0E0E0))),
            contentPadding: const EdgeInsets.symmetric(vertical: 10),
            filled: true,
            fillColor: const Color(0xFFF5F5F5),
          ),
          onSubmitted: (_) => _search(),
          onChanged: (_) => setState(() {}),
        ),
      ),
      const Divider(height: 1, color: Color(0xFFE0E0E0)),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _results.isEmpty
                ? Center(child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.person_search_outlined,
                          size: 48, color: Colors.indigo.shade200),
                      const SizedBox(height: 12),
                      const Text('Search for applicants by name or login',
                          style: TextStyle(
                              fontSize: 14, color: Color(0xFF888888))),
                    ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _results.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (_, i) {
                      final u = _results[i];
                      return Material(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(10),
                        child: ListTile(
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: 16, vertical: 4),
                          leading: CircleAvatar(
                            backgroundColor: Colors.indigo.shade50,
                            child: Text(
                              u.firstName[0].toUpperCase(),
                              style: TextStyle(
                                  color: Colors.indigo.shade600,
                                  fontWeight: FontWeight.bold),
                            ),
                          ),
                          title: Text(u.fullName,
                              style: const TextStyle(
                                  fontWeight: FontWeight.w600, fontSize: 14)),
                          subtitle: Text('@${u.login}',
                              style: const TextStyle(
                                  fontSize: 12, color: Color(0xFF666666))),
                        ),
                      );
                    },
                  ),
      ),
    ]);
  }
}
