import 'package:flutter/material.dart';

import '../../../model/vacancy.dart';

final _emailPattern = RegExp(r'^[^@\s]+@[^@\s]+\.[^@\s]+$');

String? _validateEmail(String? v) {
  if (v == null || v.trim().isEmpty) return null;
  return _emailPattern.hasMatch(v.trim()) ? null : 'Enter a valid email address';
}

/// Raw (trimmed, unconverted) values collected from [VacancyFormDialog] —
/// the caller decides how to turn these into a create vs. update request
/// body, since the two have different "omit empty" vs. "explicit null"
/// semantics.
class VacancyFormResult {
  final String title;
  final String? companyId;
  final String description;
  final String requirements;
  final String salaryFrom;
  final String salaryTo;
  final String currency;
  final EmploymentType? employmentType;
  final WorkFormat? workFormat;
  final ExperienceLevel? experienceLevel;
  final VacancyStatus status;
  final String city;
  final String country;
  final List<String> skills;
  final String contactName;
  final String contactEmail;
  final String contactPhone;

  const VacancyFormResult({
    required this.title,
    this.companyId,
    required this.description,
    required this.requirements,
    required this.salaryFrom,
    required this.salaryTo,
    required this.currency,
    required this.employmentType,
    required this.workFormat,
    required this.experienceLevel,
    required this.status,
    required this.city,
    required this.country,
    required this.skills,
    required this.contactName,
    required this.contactEmail,
    required this.contactPhone,
  });
}

/// Create/edit vacancy form, shown as an [AlertDialog]. Pass [existing] to
/// edit it (hides the Company ID field, which can't be changed after
/// creation); omit it to create a new vacancy. Pops with a
/// [VacancyFormResult] on submit, or null on cancel.
class VacancyFormDialog extends StatefulWidget {
  final Vacancy? existing;

  const VacancyFormDialog({super.key, this.existing});

  @override
  State<VacancyFormDialog> createState() => _VacancyFormDialogState();
}

class _VacancyFormDialogState extends State<VacancyFormDialog> {
  late final TextEditingController titleCtrl;
  late final TextEditingController companyIdCtrl;
  late final TextEditingController descCtrl;
  late final TextEditingController requirementsCtrl;
  late final TextEditingController salaryFromCtrl;
  late final TextEditingController salaryToCtrl;
  late final TextEditingController currencyCtrl;
  late final TextEditingController cityCtrl;
  late final TextEditingController countryCtrl;
  late final TextEditingController contactNameCtrl;
  late final TextEditingController contactEmailCtrl;
  late final TextEditingController contactPhoneCtrl;
  late final TextEditingController skillsCtrl;
  EmploymentType? employmentType;
  WorkFormat? workFormat;
  ExperienceLevel? experienceLevel;
  late VacancyStatus status;
  final formKey = GlobalKey<FormState>();

  bool get _isEditing => widget.existing != null;

  @override
  void initState() {
    super.initState();
    final v = widget.existing;
    titleCtrl = TextEditingController(text: v?.title ?? '');
    companyIdCtrl = TextEditingController();
    descCtrl = TextEditingController(text: v?.description ?? '');
    requirementsCtrl = TextEditingController(text: v?.requirements ?? '');
    salaryFromCtrl = TextEditingController(text: v?.salaryFrom?.toString() ?? '');
    salaryToCtrl = TextEditingController(text: v?.salaryTo?.toString() ?? '');
    currencyCtrl = TextEditingController(text: v?.currency ?? 'RUB');
    cityCtrl = TextEditingController(text: v?.city ?? '');
    countryCtrl = TextEditingController(text: v?.country ?? '');
    contactNameCtrl = TextEditingController(text: v?.contactName ?? '');
    contactEmailCtrl = TextEditingController(text: v?.contactEmail ?? '');
    contactPhoneCtrl = TextEditingController(text: v?.contactPhone ?? '');
    skillsCtrl = TextEditingController(text: v?.skills.join(', ') ?? '');
    employmentType = v?.employmentType;
    workFormat = v?.workFormat;
    experienceLevel = v?.experienceLevel;
    status = v?.status ?? VacancyStatus.DRAFT;
  }

  @override
  void dispose() {
    for (final c in [
      titleCtrl, companyIdCtrl, descCtrl, requirementsCtrl, salaryFromCtrl,
      salaryToCtrl, currencyCtrl, cityCtrl, countryCtrl, contactNameCtrl,
      contactEmailCtrl, contactPhoneCtrl, skillsCtrl,
    ]) {
      c.dispose();
    }
    super.dispose();
  }

  void _submit() {
    if (!formKey.currentState!.validate()) return;
    final skills = skillsCtrl.text
        .split(',')
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();
    Navigator.pop(
      context,
      VacancyFormResult(
        title: titleCtrl.text.trim(),
        companyId: _isEditing ? null : companyIdCtrl.text.trim(),
        description: descCtrl.text.trim(),
        requirements: requirementsCtrl.text.trim(),
        salaryFrom: salaryFromCtrl.text.trim(),
        salaryTo: salaryToCtrl.text.trim(),
        currency: currencyCtrl.text.trim(),
        employmentType: employmentType,
        workFormat: workFormat,
        experienceLevel: experienceLevel,
        status: status,
        city: cityCtrl.text.trim(),
        country: countryCtrl.text.trim(),
        skills: skills,
        contactName: contactNameCtrl.text.trim(),
        contactEmail: contactEmailCtrl.text.trim(),
        contactPhone: contactPhoneCtrl.text.trim(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return StatefulBuilder(
      builder: (ctx, setDialogState) => AlertDialog(
        title: Text(_isEditing ? 'Edit "${widget.existing!.title}"' : 'Add vacancy'),
        content: SizedBox(
          width: 480,
          child: SingleChildScrollView(
            child: Form(
              key: formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  TextFormField(
                    controller: titleCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Title *', border: OutlineInputBorder()),
                    validator: (v) =>
                        v == null || v.trim().isEmpty ? 'Required' : null,
                  ),
                  if (!_isEditing) ...[
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: companyIdCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Company ID *',
                          border: OutlineInputBorder()),
                      validator: (v) =>
                          v == null || v.trim().isEmpty ? 'Required' : null,
                    ),
                  ],
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: descCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Description',
                        border: OutlineInputBorder()),
                    maxLines: 3,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: requirementsCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Requirements',
                        border: OutlineInputBorder()),
                    maxLines: 3,
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: TextFormField(
                          controller: salaryFromCtrl,
                          decoration: const InputDecoration(
                              labelText: 'Salary from',
                              border: OutlineInputBorder()),
                          keyboardType: TextInputType.number,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: TextFormField(
                          controller: salaryToCtrl,
                          decoration: const InputDecoration(
                              labelText: 'Salary to',
                              border: OutlineInputBorder()),
                          keyboardType: TextInputType.number,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: TextFormField(
                          controller: currencyCtrl,
                          decoration: const InputDecoration(
                              labelText: 'Currency',
                              border: OutlineInputBorder()),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<EmploymentType>(
                    value: employmentType,
                    decoration: const InputDecoration(
                        labelText: 'Employment type',
                        border: OutlineInputBorder()),
                    items: EmploymentType.values
                        .map((e) => DropdownMenuItem(
                            value: e, child: Text(e.displayName)))
                        .toList(),
                    onChanged: (v) =>
                        setDialogState(() => employmentType = v),
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<WorkFormat>(
                    value: workFormat,
                    decoration: const InputDecoration(
                        labelText: 'Work format',
                        border: OutlineInputBorder()),
                    items: WorkFormat.values
                        .map((e) => DropdownMenuItem(
                            value: e, child: Text(e.displayName)))
                        .toList(),
                    onChanged: (v) =>
                        setDialogState(() => workFormat = v),
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<ExperienceLevel>(
                    value: experienceLevel,
                    decoration: const InputDecoration(
                        labelText: 'Experience level',
                        border: OutlineInputBorder()),
                    items: ExperienceLevel.values
                        .map((e) => DropdownMenuItem(
                            value: e, child: Text(e.displayName)))
                        .toList(),
                    onChanged: (v) =>
                        setDialogState(() => experienceLevel = v),
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<VacancyStatus>(
                    value: status,
                    decoration: const InputDecoration(
                        labelText: 'Status *',
                        border: OutlineInputBorder()),
                    items: VacancyStatus.values
                        .map((e) => DropdownMenuItem(
                            value: e, child: Text(e.displayName)))
                        .toList(),
                    onChanged: (v) =>
                        setDialogState(() => status = v ?? status),
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: cityCtrl,
                    decoration: const InputDecoration(
                        labelText: 'City', border: OutlineInputBorder()),
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: countryCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Country', border: OutlineInputBorder()),
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: skillsCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Skills (comma-separated)',
                        border: OutlineInputBorder()),
                  ),
                  const SizedBox(height: 12),
                  const Text('Contact info',
                      style: TextStyle(fontWeight: FontWeight.w600)),
                  const SizedBox(height: 8),
                  TextFormField(
                    controller: contactNameCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Name', border: OutlineInputBorder()),
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: contactEmailCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Email', border: OutlineInputBorder()),
                    validator: _validateEmail,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: contactPhoneCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Phone', border: OutlineInputBorder()),
                  ),
                ],
              ),
            ),
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, null),
              child: const Text('Cancel')),
          ElevatedButton(
            onPressed: _submit,
            child: Text(_isEditing ? 'Save' : 'Create'),
          ),
        ],
      ),
    );
  }
}
