import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../service/http_client.dart';
import '../../service/resume_api_service.dart';
import '../../service/user_api_service.dart';
import '../../theme/app_theme.dart';
import '../../util/formatters.dart';
import 'resume_form/education_section.dart';
import 'resume_form/experience_section.dart';
import 'resume_form/form_entries.dart';
import 'resume_form/form_widgets.dart';
import 'resume_form/links_section.dart';
import 'resume_form/photo_picker.dart';
import 'resume_form/skills_section.dart';

class ResumeFormScreen extends StatefulWidget {
  final ResumeApiService resumeApiService;
  final UserApiService userApiService;
  final Resume? resume;

  const ResumeFormScreen({
    super.key,
    required this.resumeApiService,
    required this.userApiService,
    this.resume,
  });

  @override
  State<ResumeFormScreen> createState() => _ResumeFormScreenState();
}

class _ResumeFormScreenState extends State<ResumeFormScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isSaving = false;

  late final TextEditingController _firstName;
  late final TextEditingController _lastName;
  late final TextEditingController _email;
  late final TextEditingController _phone;
  late final TextEditingController _desiredPosition;
  late final TextEditingController _summary;
  late final TextEditingController _birthDate;

  Uint8List? _photoBytes;
  String? _photoFilename;

  final List<String> _skills = [];

  final List<ExpEntry> _experiences = [];
  final List<EduEntry> _educations = [];
  final List<TextEditingController> _links = [];

  bool get _isEditing => widget.resume != null;

  @override
  void initState() {
    super.initState();
    final r = widget.resume;
    _firstName = TextEditingController(text: r?.firstName ?? '');
    _lastName = TextEditingController(text: r?.lastName ?? '');
    _email = TextEditingController(text: r?.email ?? '');
    _phone = TextEditingController(text: r?.phone ?? '');
    _desiredPosition = TextEditingController(text: r?.desiredPosition ?? '');
    _summary = TextEditingController(text: r?.summary ?? '');
    _birthDate = TextEditingController(text: r?.birthDate ?? '');
    if (r != null) {
      _skills.addAll(r.skills);
      _experiences.addAll(r.experience.map(ExpEntry.fromModel));
      _educations.addAll(r.education.map(EduEntry.fromModel));
      _links.addAll(r.links.map((l) => TextEditingController(text: l)));
    }
    if (!_isEditing) {
      _prefillFromProfile();
    }
  }

  Future<void> _prefillFromProfile() async {
    final profile = await widget.userApiService.getMyProfile();
    if (profile != null && mounted) {
      setState(() {
        if (_firstName.text.isEmpty) _firstName.text = profile.firstName;
        if (_lastName.text.isEmpty) _lastName.text = profile.lastName;
        if (_birthDate.text.isEmpty) _birthDate.text = profile.birthDate;
      });
    }
  }

  @override
  void dispose() {
    for (final c in [
      _firstName, _lastName, _email, _phone, _desiredPosition, _summary,
      _birthDate
    ]) {
      c.dispose();
    }
    for (final e in _experiences) e.dispose();
    for (final e in _educations) e.dispose();
    for (final c in _links) c.dispose();
    super.dispose();
  }

  Future<void> _pickDate(TextEditingController c) async {
    final initial = _parseDate(c.text) ?? DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: initial,
      firstDate: DateTime(1950),
      lastDate: DateTime.now(),
    );
    if (picked != null && mounted) {
      setState(() {
        c.text = formatDate(picked);
      });
    }
  }

  DateTime? _parseDate(String v) {
    try {
      return DateTime.parse(v);
    } catch (_) {
      return null;
    }
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    if (_skills.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Add at least one skill')),
      );
      return;
    }
    final body = <String, dynamic>{
      'firstName': _firstName.text.trim(),
      'lastName': _lastName.text.trim(),
      'email': _email.text.trim(),
      if (_phone.text.trim().isNotEmpty) 'phone': _phone.text.trim(),
      if (_desiredPosition.text.trim().isNotEmpty)
        'desiredPosition': _desiredPosition.text.trim(),
      if (_summary.text.trim().isNotEmpty) 'summary': _summary.text.trim(),
      if (_birthDate.text.isNotEmpty) 'birthDate': _birthDate.text,
      'skills': _skills,
      if (_experiences.isNotEmpty)
        'experience': _experiences.map((e) => e.toJson()).toList(),
      if (_educations.isNotEmpty)
        'education': _educations.map((e) => e.toJson()).toList(),
      if (_links.isNotEmpty)
        'links': _links.map((c) => c.text.trim()).where((l) => l.isNotEmpty).toList(),
    };

    setState(() => _isSaving = true);
    final result = _isEditing
        ? await widget.resumeApiService.update(widget.resume!.id, body)
        : await widget.resumeApiService.create(body);

    if (!mounted) return;

    String? photoError;
    if (result != null && _photoBytes != null) {
      try {
        await widget.resumeApiService.uploadPhoto(
          result.id,
          _photoBytes!,
          _photoFilename ?? 'photo.jpg',
        );
      } on ApiException catch (e) {
        photoError = e.message;
      }
    }

    setState(() => _isSaving = false);
    if (!mounted) return;

    if (result != null) {
      if (photoError != null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Resume saved, but photo upload failed: $photoError')),
        );
      }
      Navigator.of(context).pop(true);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text(_isEditing
                ? 'Failed to update resume'
                : 'Failed to create resume')),
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
        surfaceTintColor: AppColors.surface,
        title: Text(
          _isEditing ? 'Edit Resume' : 'New Resume',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: AppColors.border),
        ),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
          children: [
            FormSection(
              icon: Icons.person_outline,
              title: 'Personal Info',
              child: Column(
                children: [
                  PhotoPicker(
                    photoBytes: _photoBytes,
                    existingPhotoUrl: widget.resume?.photoUrl,
                    onPicked: (bytes, filename, dataUrl) {
                      setState(() {
                        _photoBytes = bytes;
                        _photoFilename = filename;
                      });
                    },
                  ),
                  const SizedBox(height: 4),
                  resumeFieldRow(
                    resumeTextField(_firstName, 'First name', required: true),
                    resumeTextField(_lastName, 'Last name', required: true),
                  ),
                  resumeTextField(
                    _desiredPosition,
                    'Desired position',
                    hint: 'e.g. Backend Developer',
                  ),
                  resumeDateField(_birthDate, 'Date of birth',
                      onTap: () => _pickDate(_birthDate)),
                ],
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.contact_phone_outlined,
              title: 'Contacts',
              child: Column(
                children: [
                  resumeTextField(
                    _email,
                    'Email',
                    required: true,
                    keyboardType: TextInputType.emailAddress,
                    validator: (v) {
                      if (v == null || v.trim().isEmpty) return 'Required';
                      if (!v.contains('@')) return 'Invalid email';
                      return null;
                    },
                  ),
                  resumeTextField(_phone, 'Phone', hint: '+7 (999) 123-45-67'),
                ],
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.notes_outlined,
              title: 'About me',
              child: resumeTextField(
                _summary,
                'Professional summary',
                hint: 'Brief description of your experience and goals...',
                maxLines: 4,
                maxLength: 3000,
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.star_outline,
              title: 'Skills',
              child: SkillsSection(
                skills: _skills,
                onAdd: (s) => setState(() => _skills.add(s)),
                onRemove: (s) => setState(() => _skills.remove(s)),
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.work_outline,
              title: 'Work Experience',
              child: ExperienceSection(
                entries: _experiences,
                onPickDate: _pickDate,
                onRemove: (i) => setState(() {
                  _experiences[i].dispose();
                  _experiences.removeAt(i);
                }),
                onAdd: () => setState(() => _experiences.add(ExpEntry.empty())),
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.school_outlined,
              title: 'Education',
              child: EducationSection(
                entries: _educations,
                onPickDate: _pickDate,
                onRemove: (i) => setState(() {
                  _educations[i].dispose();
                  _educations.removeAt(i);
                }),
                onAdd: () => setState(() => _educations.add(EduEntry.empty())),
              ),
            ),
            const SizedBox(height: 12),
            FormSection(
              icon: Icons.link_outlined,
              title: 'Links',
              child: LinksSection(
                links: _links,
                onRemove: (i) => setState(() {
                  _links[i].dispose();
                  _links.removeAt(i);
                }),
                onAdd: () => setState(() => _links.add(TextEditingController())),
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: Container(
        color: AppColors.surface,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 28),
        child: FilledButton(
          onPressed: _isSaving ? null : _save,
          style: FilledButton.styleFrom(
            backgroundColor: AppColors.accent,
            minimumSize: const Size.fromHeight(52),
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          ),
          child: _isSaving
              ? SizedBox(
                  width: 22,
                  height: 22,
                  child: CircularProgressIndicator(
                      strokeWidth: 2, color: AppColors.surface),
                )
              : Text(
                  _isEditing ? 'Save changes' : 'Create resume',
                  style: const TextStyle(
                      fontSize: 16, fontWeight: FontWeight.w600),
                ),
        ),
      ),
    );
  }
}
