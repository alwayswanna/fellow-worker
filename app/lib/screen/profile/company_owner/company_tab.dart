import 'package:flutter/material.dart';

import '../../../model/vacancy.dart';
import '../../../service/vacancy_api_service.dart';
import '../../../theme/app_theme.dart';
import '../../../widgets/widgets.dart';

class CompanyTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final void Function(Company?) onCompanyChanged;

  const CompanyTab({
    super.key,
    required this.company,
    required this.vacancyApiService,
    required this.onCompanyChanged,
  });

  @override
  State<CompanyTab> createState() => _CompanyTabState();
}

class _CompanyTabState extends State<CompanyTab> {
  bool _isEditing = false;

  @override
  Widget build(BuildContext context) {
    if (widget.company == null || _isEditing) {
      return CompanyForm(
        company: widget.company,
        vacancyApiService: widget.vacancyApiService,
        onSaved: (c) {
          setState(() => _isEditing = false);
          widget.onCompanyChanged(c);
        },
        onCancel: widget.company != null ? () => setState(() => _isEditing = false) : null,
      );
    }
    return CompanyInfo(
      company: widget.company!,
      onEdit: () => setState(() => _isEditing = true),
    );
  }
}

// ── Company info ───────────────────────────────────────────────────────────────

class CompanyInfo extends StatelessWidget {
  final Company company;
  final VoidCallback onEdit;

  const CompanyInfo({super.key, required this.company, required this.onEdit});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              InitialsAvatar(
                name: company.name,
                radius: 28,
                borderRadius: 12,
                color: AppColors.avatarColor(company.name).withAlpha(38),
                textColor: AppColors.avatarColor(company.name),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(company.name,
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  if (company.industry != null)
                    Text(company.industry!,
                        style: TextStyle(fontSize: 14, color: AppColors.textSecondary)),
                ]),
              ),
              OutlinedButton.icon(
                onPressed: onEdit,
                icon: const Icon(Icons.edit_outlined, size: 16),
                label: const Text('Edit'),
                style: OutlinedButton.styleFrom(
                  foregroundColor: AppColors.primary,
                  side: BorderSide(color: AppColors.primary),
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
                DetailChip(Icons.location_on_outlined, company.city!),
              if (company.country != null)
                DetailChip(Icons.flag_outlined, company.country!),
              if (company.size != null)
                DetailChip(Icons.people_outline, company.size!.displayName),
              DetailChip(Icons.star_outline,
                  '${company.rating.toStringAsFixed(1)} (${company.reviewCount} reviews)',
                  color: AppColors.warning),
            ],
          ),
          if (company.website != null && company.website!.isNotEmpty) ...[
            const SizedBox(height: 16),
            Row(children: [
              Icon(Icons.link, size: 16, color: AppColors.textSecondary),
              const SizedBox(width: 6),
              Text(company.website!,
                  style: TextStyle(fontSize: 14, color: AppColors.primary)),
            ]),
          ],
          if (company.description != null && company.description!.isNotEmpty) ...[
            const SizedBox(height: 20),
            const Text('About', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
            const SizedBox(height: 8),
            Text(company.description!,
                style: TextStyle(fontSize: 14, height: 1.5, color: AppColors.text)),
          ],
        ],
      ),
    );
  }
}

// ── Company form ───────────────────────────────────────────────────────────────

class CompanyForm extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final void Function(Company) onSaved;
  final VoidCallback? onCancel;

  const CompanyForm({
    super.key,
    required this.company,
    required this.vacancyApiService,
    required this.onSaved,
    this.onCancel,
  });

  @override
  State<CompanyForm> createState() => _CompanyFormState();
}

class _CompanyFormState extends State<CompanyForm> {
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
                color: AppColors.errorBg, borderRadius: BorderRadius.circular(8),
                border: Border.all(color: AppColors.error.withAlpha(80)),
              ),
              child: Text(_error!, style: TextStyle(color: AppColors.error, fontSize: 13)),
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
                    foregroundColor: AppColors.textSecondary,
                    side: BorderSide(color: AppColors.border),
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
                  backgroundColor: AppColors.accent,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                ),
                child: _isSaving
                    ? SizedBox(width: 18, height: 18,
                        child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.surface))
                    : Text(widget.company == null ? 'Create' : 'Save'),
              ),
            ),
          ]),
        ]),
      ),
    );
  }
}
