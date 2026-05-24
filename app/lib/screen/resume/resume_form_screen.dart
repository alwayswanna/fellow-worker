import 'dart:convert';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../model/resume.dart';
import '../../service/resume_api_service.dart';
import '../../service/user_api_service.dart';
import '../../util/formatters.dart';

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
  String? _photoDataUrl; // for preview

  final List<String> _skills = [];
  final TextEditingController _skillInput = TextEditingController();

  final List<_ExpEntry> _experiences = [];
  final List<_EduEntry> _educations = [];
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
      _experiences.addAll(r.experience.map(_ExpEntry.fromModel));
      _educations.addAll(r.education.map(_EduEntry.fromModel));
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
      _birthDate, _skillInput
    ]) {
      c.dispose();
    }
    for (final e in _experiences) e.dispose();
    for (final e in _educations) e.dispose();
    for (final c in _links) c.dispose();
    super.dispose();
  }

  void _addSkill() {
    final s = _skillInput.text.trim();
    if (s.isEmpty || _skills.contains(s)) return;
    setState(() {
      _skills.add(s);
      _skillInput.clear();
    });
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

  void _pickPhoto() {
    final input = html.FileUploadInputElement()..accept = 'image/*';
    input.click();
    input.onChange.listen((event) {
      final file = input.files?.first;
      if (file == null) return;
      final reader = html.FileReader();
      reader.readAsDataUrl(file);
      reader.onLoadEnd.listen((_) {
        final dataUrl = reader.result as String;
        final base64 = dataUrl.split(',').last;
        final bytes = base64Decode(base64);
        if (mounted) {
          setState(() {
            _photoBytes = bytes;
            _photoFilename = file.name;
            _photoDataUrl = dataUrl;
          });
        }
      });
    });
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

    if (result != null && _photoBytes != null) {
      await widget.resumeApiService.uploadPhoto(
        result.id,
        _photoBytes!,
        _photoFilename ?? 'photo.jpg',
      );
    }

    setState(() => _isSaving = false);
    if (!mounted) return;

    if (result != null) {
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
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF232F3E),
        elevation: 0,
        surfaceTintColor: Colors.white,
        title: Text(
          _isEditing ? 'Edit Resume' : 'New Resume',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, thickness: 1, color: Color(0xFFE0E0E0)),
        ),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
          children: [
            _FormSection(
              icon: Icons.person_outline,
              title: 'Personal Info',
              child: Column(
                children: [
                  _buildPhotoPicker(),
                  const SizedBox(height: 4),
                  _rowFields(
                    _field(_firstName, 'First name', required: true),
                    _field(_lastName, 'Last name', required: true),
                  ),
                  _field(
                    _desiredPosition,
                    'Desired position',
                    hint: 'e.g. Backend Developer',
                  ),
                  _dateField(_birthDate, 'Date of birth'),
                ],
              ),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.contact_phone_outlined,
              title: 'Contacts',
              child: Column(
                children: [
                  _field(
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
                  _field(_phone, 'Phone', hint: '+7 (999) 123-45-67'),
                ],
              ),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.notes_outlined,
              title: 'About me',
              child: _field(
                _summary,
                'Professional summary',
                hint: 'Brief description of your experience and goals...',
                maxLines: 4,
                maxLength: 3000,
              ),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.star_outline,
              title: 'Skills',
              child: _buildSkillsSection(),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.work_outline,
              title: 'Work Experience',
              child: _buildExperienceSection(),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.school_outlined,
              title: 'Education',
              child: _buildEducationSection(),
            ),
            const SizedBox(height: 12),
            _FormSection(
              icon: Icons.link_outlined,
              title: 'Links',
              child: _buildLinksSection(),
            ),
          ],
        ),
      ),
      bottomNavigationBar: Container(
        color: Colors.white,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 28),
        child: FilledButton(
          onPressed: _isSaving ? null : _save,
          style: FilledButton.styleFrom(
            backgroundColor: Colors.indigo,
            minimumSize: const Size.fromHeight(52),
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          ),
          child: _isSaving
              ? const SizedBox(
                  width: 22,
                  height: 22,
                  child: CircularProgressIndicator(
                      strokeWidth: 2, color: Colors.white),
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

  // ── Photo picker ──────────────────────────────────────────────────────────

  Widget _buildPhotoPicker() {
    final existingPhotoUrl = widget.resume?.photoUrl;
    final hasPreview = _photoDataUrl != null || existingPhotoUrl != null;

    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Center(
        child: GestureDetector(
          onTap: _pickPhoto,
          child: Stack(
            children: [
              CircleAvatar(
                radius: 48,
                backgroundColor: const Color(0xFFEEF2FF),
                backgroundImage: _photoBytes != null
                    ? MemoryImage(_photoBytes!)
                    : (existingPhotoUrl != null
                        ? NetworkImage(existingPhotoUrl)
                        : null),
                child: !hasPreview
                    ? Icon(Icons.person, size: 48, color: Colors.indigo.shade200)
                    : null,
              ),
              Positioned(
                bottom: 0,
                right: 0,
                child: Container(
                  width: 28,
                  height: 28,
                  decoration: BoxDecoration(
                    color: Colors.indigo,
                    shape: BoxShape.circle,
                    border: Border.all(color: Colors.white, width: 2),
                  ),
                  child: const Icon(Icons.camera_alt, size: 14, color: Colors.white),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  Widget _rowFields(Widget left, Widget right) => Row(
        children: [
          Expanded(child: left),
          const SizedBox(width: 12),
          Expanded(child: right),
        ],
      );

  Widget _field(
    TextEditingController c,
    String label, {
    bool required = false,
    int? maxLength,
    int maxLines = 1,
    String? hint,
    TextInputType? keyboardType,
    String? Function(String?)? validator,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextFormField(
        controller: c,
        maxLength: maxLength,
        maxLines: maxLines,
        keyboardType: keyboardType,
        decoration: InputDecoration(
          labelText: required ? '$label *' : label,
          hintText: hint,
          hintStyle: const TextStyle(color: Color(0xFFBBBBBB)),
          filled: true,
          fillColor: const Color(0xFFFAFAFA),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.indigo, width: 1.5),
          ),
          isDense: true,
          counterText: '',
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
        ),
        validator: validator ??
            (required
                ? (v) =>
                    (v == null || v.trim().isEmpty) ? 'Required field' : null
                : null),
      ),
    );
  }

  Widget _dateField(TextEditingController c, String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextFormField(
        controller: c,
        readOnly: true,
        decoration: InputDecoration(
          labelText: label,
          filled: true,
          fillColor: const Color(0xFFFAFAFA),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: const BorderSide(color: Colors.indigo, width: 1.5),
          ),
          isDense: true,
          suffixIcon: const Icon(Icons.calendar_month_outlined,
              size: 16, color: Color(0xFF999999)),
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
        ),
        onTap: () => _pickDate(c),
      ),
    );
  }

  // ── Skills ────────────────────────────────────────────────────────────────

  Widget _buildSkillsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: TextFormField(
                controller: _skillInput,
                decoration: InputDecoration(
                  hintText: 'e.g. Java, Spring Boot, PostgreSQL',
                  hintStyle: const TextStyle(color: Color(0xFFBBBBBB)),
                  filled: true,
                  fillColor: const Color(0xFFFAFAFA),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide:
                        const BorderSide(color: Colors.indigo, width: 1.5),
                  ),
                  isDense: true,
                  contentPadding:
                      const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
                ),
                onFieldSubmitted: (_) => _addSkill(),
              ),
            ),
            const SizedBox(width: 8),
            SizedBox(
              height: 48,
              child: FilledButton(
                onPressed: _addSkill,
                style: FilledButton.styleFrom(
                  backgroundColor: Colors.indigo,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                ),
                child: const Text('Add'),
              ),
            ),
          ],
        ),
        if (_skills.isNotEmpty) ...[
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: _skills
                .map((s) => Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(
                        color: const Color(0xFFEEF2FF),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(s,
                              style: TextStyle(
                                  fontSize: 13,
                                  color: Colors.indigo.shade700,
                                  fontWeight: FontWeight.w500)),
                          const SizedBox(width: 6),
                          GestureDetector(
                            onTap: () => setState(() => _skills.remove(s)),
                            child: Icon(Icons.close,
                                size: 14, color: Colors.indigo.shade400),
                          ),
                        ],
                      ),
                    ))
                .toList(),
          ),
        ],
      ],
    );
  }

  // ── Work Experience ───────────────────────────────────────────────────────

  Widget _buildExperienceSection() {
    return Column(
      children: [
        ..._experiences.asMap().entries.map((e) => _ExpCard(
              entry: e.value,
              index: e.key,
              onRemove: () => setState(() {
                _experiences[e.key].dispose();
                _experiences.removeAt(e.key);
              }),
              onPickDate: _pickDate,
            )),
        _addButton('Add work experience',
            () => setState(() => _experiences.add(_ExpEntry.empty()))),
      ],
    );
  }

  // ── Education ─────────────────────────────────────────────────────────────

  Widget _buildEducationSection() {
    return Column(
      children: [
        ..._educations.asMap().entries.map((e) => _EduCard(
              entry: e.value,
              index: e.key,
              onRemove: () => setState(() {
                _educations[e.key].dispose();
                _educations.removeAt(e.key);
              }),
              onPickDate: _pickDate,
            )),
        _addButton('Add education',
            () => setState(() => _educations.add(_EduEntry.empty()))),
      ],
    );
  }

  // ── Links ─────────────────────────────────────────────────────────────────

  Widget _buildLinksSection() {
    return Column(
      children: [
        ..._links.asMap().entries.map((e) => Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: e.value,
                      keyboardType: TextInputType.url,
                      decoration: InputDecoration(
                        hintText: 'https://linkedin.com/in/...',
                        hintStyle:
                            const TextStyle(color: Color(0xFFBBBBBB)),
                        prefixIcon: const Icon(Icons.link,
                            size: 18, color: Color(0xFF999999)),
                        filled: true,
                        fillColor: const Color(0xFFFAFAFA),
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide:
                                const BorderSide(color: Color(0xFFDDDDDD))),
                        enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide:
                                const BorderSide(color: Color(0xFFDDDDDD))),
                        focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide: const BorderSide(
                                color: Colors.indigo, width: 1.5)),
                        isDense: true,
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: 14, vertical: 14),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  GestureDetector(
                    onTap: () => setState(() {
                      _links[e.key].dispose();
                      _links.removeAt(e.key);
                    }),
                    child: const Icon(Icons.remove_circle_outline,
                        color: Colors.red, size: 22),
                  ),
                ],
              ),
            )),
        _addButton(
            'Add link', () => setState(() => _links.add(TextEditingController()))),
      ],
    );
  }

  Widget _addButton(String label, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          border: Border.all(
              color: Colors.indigo.withValues(alpha: 0.4), style: BorderStyle.solid),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.add, size: 18, color: Colors.indigo.shade600),
            const SizedBox(width: 6),
            Text(label,
                style: TextStyle(
                    color: Colors.indigo.shade600,
                    fontWeight: FontWeight.w500)),
          ],
        ),
      ),
    );
  }
}

// ── Form section wrapper ───────────────────────────────────────────────────

class _FormSection extends StatelessWidget {
  final IconData icon;
  final String title;
  final Widget child;

  const _FormSection({
    required this.icon,
    required this.title,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
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
                Text(title,
                    style: const TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF232F3E))),
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

// ── Experience entry ───────────────────────────────────────────────────────

class _ExpEntry {
  final TextEditingController company;
  final TextEditingController position;
  final TextEditingController startDate;
  final TextEditingController endDate;
  final TextEditingController description;

  _ExpEntry({
    required this.company,
    required this.position,
    required this.startDate,
    required this.endDate,
    required this.description,
  });

  factory _ExpEntry.empty() => _ExpEntry(
        company: TextEditingController(),
        position: TextEditingController(),
        startDate: TextEditingController(),
        endDate: TextEditingController(),
        description: TextEditingController(),
      );

  factory _ExpEntry.fromModel(WorkExperience m) => _ExpEntry(
        company: TextEditingController(text: m.company),
        position: TextEditingController(text: m.position),
        startDate: TextEditingController(text: m.startDate ?? ''),
        endDate: TextEditingController(text: m.endDate ?? ''),
        description: TextEditingController(text: m.description ?? ''),
      );

  Map<String, dynamic> toJson() => {
        'company': company.text.trim(),
        'position': position.text.trim(),
        if (startDate.text.isNotEmpty) 'startDate': startDate.text,
        if (endDate.text.isNotEmpty) 'endDate': endDate.text,
        if (description.text.trim().isNotEmpty)
          'description': description.text.trim(),
      };

  void dispose() {
    company.dispose();
    position.dispose();
    startDate.dispose();
    endDate.dispose();
    description.dispose();
  }
}

// ── Education entry ────────────────────────────────────────────────────────

class _EduEntry {
  final TextEditingController institution;
  final TextEditingController degree;
  final TextEditingController fieldOfStudy;
  final TextEditingController startDate;
  final TextEditingController endDate;

  _EduEntry({
    required this.institution,
    required this.degree,
    required this.fieldOfStudy,
    required this.startDate,
    required this.endDate,
  });

  factory _EduEntry.empty() => _EduEntry(
        institution: TextEditingController(),
        degree: TextEditingController(),
        fieldOfStudy: TextEditingController(),
        startDate: TextEditingController(),
        endDate: TextEditingController(),
      );

  factory _EduEntry.fromModel(Education m) => _EduEntry(
        institution: TextEditingController(text: m.institution),
        degree: TextEditingController(text: m.degree),
        fieldOfStudy: TextEditingController(text: m.fieldOfStudy),
        startDate: TextEditingController(text: m.startDate ?? ''),
        endDate: TextEditingController(text: m.endDate ?? ''),
      );

  Map<String, dynamic> toJson() => {
        'institution': institution.text.trim(),
        'degree': degree.text.trim(),
        'fieldOfStudy': fieldOfStudy.text.trim(),
        if (startDate.text.isNotEmpty) 'startDate': startDate.text,
        if (endDate.text.isNotEmpty) 'endDate': endDate.text,
      };

  void dispose() {
    institution.dispose();
    degree.dispose();
    fieldOfStudy.dispose();
    startDate.dispose();
    endDate.dispose();
  }
}

// ── Experience card in form ────────────────────────────────────────────────

class _ExpCard extends StatelessWidget {
  final _ExpEntry entry;
  final int index;
  final VoidCallback onRemove;
  final Future<void> Function(TextEditingController) onPickDate;

  const _ExpCard({
    required this.entry,
    required this.index,
    required this.onRemove,
    required this.onPickDate,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFFAFAFA),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFE8E8E8)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 28,
                height: 28,
                decoration: BoxDecoration(
                  color: Colors.indigo.shade50,
                  shape: BoxShape.circle,
                ),
                alignment: Alignment.center,
                child: Text('${index + 1}',
                    style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: Colors.indigo.shade700)),
              ),
              const SizedBox(width: 8),
              Text('Experience ${index + 1}',
                  style: const TextStyle(
                      fontWeight: FontWeight.w600, fontSize: 14,
                      color: Color(0xFF444444))),
              const Spacer(),
              GestureDetector(
                onTap: onRemove,
                child: const Icon(Icons.close, size: 18, color: Colors.red),
              ),
            ],
          ),
          const SizedBox(height: 12),
          _f(entry.company, 'Company', required: true),
          _f(entry.position, 'Position / Role', required: true),
          Row(
            children: [
              Expanded(child: _dateF(entry.startDate, 'Start date')),
              const Padding(
                padding: EdgeInsets.symmetric(horizontal: 8),
                child: Text('—',
                    style: TextStyle(color: Color(0xFF999999), fontSize: 18)),
              ),
              Expanded(child: _dateF(entry.endDate, 'End date')),
            ],
          ),
          _f(entry.description, 'Responsibilities & achievements',
              maxLines: 3),
        ],
      ),
    );
  }

  Widget _f(TextEditingController c, String label,
      {bool required = false, int maxLines = 1}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        maxLines: maxLines,
        decoration: InputDecoration(
          labelText: required ? '$label *' : label,
          filled: true,
          fillColor: Colors.white,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Colors.indigo, width: 1.5)),
          isDense: true,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        validator: required
            ? (v) => (v == null || v.trim().isEmpty) ? 'Required' : null
            : null,
      ),
    );
  }

  Widget _dateF(TextEditingController c, String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        readOnly: true,
        decoration: InputDecoration(
          labelText: label,
          filled: true,
          fillColor: Colors.white,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Colors.indigo, width: 1.5)),
          isDense: true,
          suffixIcon:
              const Icon(Icons.calendar_month_outlined, size: 16, color: Color(0xFF999999)),
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        onTap: () => onPickDate(c),
      ),
    );
  }
}

// ── Education card in form ─────────────────────────────────────────────────

class _EduCard extends StatelessWidget {
  final _EduEntry entry;
  final int index;
  final VoidCallback onRemove;
  final Future<void> Function(TextEditingController) onPickDate;

  const _EduCard({
    required this.entry,
    required this.index,
    required this.onRemove,
    required this.onPickDate,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFFAFAFA),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFE8E8E8)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 28,
                height: 28,
                decoration: BoxDecoration(
                  color: Colors.indigo.shade50,
                  shape: BoxShape.circle,
                ),
                alignment: Alignment.center,
                child: Text('${index + 1}',
                    style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: Colors.indigo.shade700)),
              ),
              const SizedBox(width: 8),
              Text('Education ${index + 1}',
                  style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 14,
                      color: Color(0xFF444444))),
              const Spacer(),
              GestureDetector(
                onTap: onRemove,
                child: const Icon(Icons.close, size: 18, color: Colors.red),
              ),
            ],
          ),
          const SizedBox(height: 12),
          _f(entry.institution, 'Institution', required: true),
          _f(entry.degree, 'Degree', required: true),
          _f(entry.fieldOfStudy, 'Field of study', required: true),
          Row(
            children: [
              Expanded(child: _dateF(entry.startDate, 'Start date')),
              const Padding(
                padding: EdgeInsets.symmetric(horizontal: 8),
                child: Text('—',
                    style:
                        TextStyle(color: Color(0xFF999999), fontSize: 18)),
              ),
              Expanded(child: _dateF(entry.endDate, 'End date')),
            ],
          ),
        ],
      ),
    );
  }

  Widget _f(TextEditingController c, String label, {bool required = false}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        decoration: InputDecoration(
          labelText: required ? '$label *' : label,
          filled: true,
          fillColor: Colors.white,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Colors.indigo, width: 1.5)),
          isDense: true,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        validator: required
            ? (v) => (v == null || v.trim().isEmpty) ? 'Required' : null
            : null,
      ),
    );
  }

  Widget _dateF(TextEditingController c, String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextFormField(
        controller: c,
        readOnly: true,
        decoration: InputDecoration(
          labelText: label,
          filled: true,
          fillColor: Colors.white,
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFDDDDDD))),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Colors.indigo, width: 1.5)),
          isDense: true,
          suffixIcon: const Icon(Icons.calendar_month_outlined,
              size: 16, color: Color(0xFF999999)),
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        onTap: () => onPickDate(c),
      ),
    );
  }
}
