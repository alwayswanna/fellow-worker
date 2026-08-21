import 'package:flutter/material.dart';

import '../../../model/vacancy.dart';
import '../../../service/http_client.dart';
import '../../../service/vacancy_api_service.dart';
import '../../../theme/app_theme.dart';

/// Create/edit vacancy dialog used by [VacanciesTab] (company owner and
/// recruiter panels). Pops `true` on a successful save, `false`/null on
/// cancel.
class VacancyFormDialog extends StatefulWidget {
  final String companyId;
  final VacancyApiService vacancyApiService;
  final Vacancy? existing;

  const VacancyFormDialog(
      {super.key, required this.companyId, required this.vacancyApiService, this.existing});

  @override
  State<VacancyFormDialog> createState() => _VacancyFormDialogState();
}

class _VacancyFormDialogState extends State<VacancyFormDialog> {
  final _formKey = GlobalKey<FormState>();
  late final _titleCtrl = TextEditingController(text: widget.existing?.title ?? '');
  late final _descCtrl = TextEditingController(text: widget.existing?.description ?? '');
  late final _reqCtrl = TextEditingController(text: widget.existing?.requirements ?? '');
  late final _salaryFromCtrl =
      TextEditingController(text: widget.existing?.salaryFrom?.toString() ?? '');
  late final _salaryToCtrl =
      TextEditingController(text: widget.existing?.salaryTo?.toString() ?? '');
  late final _currencyCtrl = TextEditingController(text: widget.existing?.currency ?? 'RUB');
  late final _cityCtrl = TextEditingController(text: widget.existing?.city ?? '');
  late final _countryCtrl = TextEditingController(text: widget.existing?.country ?? '');
  late final _skillsCtrl =
      TextEditingController(text: widget.existing?.skills.join(', ') ?? '');
  late final _contactNameCtrl =
      TextEditingController(text: widget.existing?.contactName ?? '');
  late final _contactEmailCtrl =
      TextEditingController(text: widget.existing?.contactEmail ?? '');
  late final _contactPhoneCtrl =
      TextEditingController(text: widget.existing?.contactPhone ?? '');
  late EmploymentType? _employmentType = widget.existing?.employmentType;
  late WorkFormat? _workFormat = widget.existing?.workFormat;
  late ExperienceLevel? _experienceLevel = widget.existing?.experienceLevel;
  late VacancyStatus _status = widget.existing?.status ?? VacancyStatus.ACTIVE;
  bool _isSaving = false;

  bool get _isEditing => widget.existing != null;

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
      'status': _status.name,
      if (_contactNameCtrl.text.trim().isNotEmpty) 'contactName': _contactNameCtrl.text.trim(),
      if (_contactEmailCtrl.text.trim().isNotEmpty) 'contactEmail': _contactEmailCtrl.text.trim(),
      if (_contactPhoneCtrl.text.trim().isNotEmpty) 'contactPhone': _contactPhoneCtrl.text.trim(),
    };
    try {
      if (_isEditing) {
        await widget.vacancyApiService.updateVacancy(widget.existing!.id, body);
      } else {
        await widget.vacancyApiService.createVacancy(body);
      }
      if (mounted) Navigator.of(context).pop(true);
    } catch (e) {
      if (mounted) {
        setState(() => _isSaving = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e is ApiException
              ? e.message
              : _isEditing ? 'Failed to update vacancy' : 'Failed to create vacancy')),
        );
      }
    }
  }

  static final _emailPattern = RegExp(r'^[^@\s]+@[^@\s]+\.[^@\s]+$');

  String? _validateEmail(String? v) {
    if (v == null || v.trim().isEmpty) return null;
    return _emailPattern.hasMatch(v.trim()) ? null : 'Enter a valid email address';
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
      title: Text(_isEditing ? 'Edit vacancy' : 'New vacancy'),
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
            DropdownButtonFormField<VacancyStatus>(
              value: _status, decoration: _dec('Status *'),
              items: VacancyStatus.values
                  .map((e) => DropdownMenuItem(value: e, child: Text(e.displayName))).toList(),
              onChanged: (v) => setState(() => _status = v ?? _status),
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
                keyboardType: TextInputType.emailAddress,
                validator: _validateEmail),
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
          style: FilledButton.styleFrom(backgroundColor: AppColors.accent),
          child: _isSaving
              ? SizedBox(width: 16, height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.surface))
              : Text(_isEditing ? 'Save' : 'Create'),
        ),
      ],
    );
  }
}
