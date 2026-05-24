import 'package:flutter/material.dart';

import '../../model/vacancy.dart';
import '../../service/vacancy_api_service.dart';
import '../../util/formatters.dart';

class AdminVacanciesTab extends StatefulWidget {
  final VacancyApiService vacancyApiService;

  const AdminVacanciesTab({super.key, required this.vacancyApiService});

  @override
  State<AdminVacanciesTab> createState() => _AdminVacanciesTabState();
}

class _AdminVacanciesTabState extends State<AdminVacanciesTab> {
  final List<Vacancy> _vacancies = [];
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _searchController = TextEditingController();
  VacancyStatus _statusFilter = VacancyStatus.ACTIVE;
  bool _isLoading = false;
  bool _isLoadingMore = false;
  bool _hasMore = true;
  int _page = 0;
  static const int _pageSize = 20;

  @override
  void initState() {
    super.initState();
    _loadPage(reset: true);
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent - 200 &&
        !_isLoadingMore &&
        _hasMore) {
      _loadPage();
    }
  }

  Future<void> _loadPage({bool reset = false}) async {
    if (reset) {
      setState(() {
        _isLoading = true;
        _vacancies.clear();
        _page = 0;
        _hasMore = true;
      });
    } else {
      setState(() => _isLoadingMore = true);
    }

    final results = await widget.vacancyApiService.searchVacancies(
      title: _searchController.text.trim().isEmpty
          ? null
          : _searchController.text.trim(),
      status: _statusFilter,
      page: _page,
      size: _pageSize,
    );

    if (mounted) {
      setState(() {
        _vacancies.addAll(results);
        _hasMore = results.length >= _pageSize;
        _page++;
        _isLoading = false;
        _isLoadingMore = false;
      });
    }
  }

  Future<void> _showAddDialog() async {
    final titleCtrl = TextEditingController();
    final companyIdCtrl = TextEditingController();
    final descCtrl = TextEditingController();
    final requirementsCtrl = TextEditingController();
    final salaryFromCtrl = TextEditingController();
    final salaryToCtrl = TextEditingController();
    final currencyCtrl = TextEditingController(text: 'RUB');
    final cityCtrl = TextEditingController();
    final countryCtrl = TextEditingController();
    final contactNameCtrl = TextEditingController();
    final contactEmailCtrl = TextEditingController();
    final contactPhoneCtrl = TextEditingController();
    final skillsCtrl = TextEditingController();
    EmploymentType? employmentType;
    WorkFormat? workFormat;
    ExperienceLevel? experienceLevel;
    VacancyStatus status = VacancyStatus.DRAFT;
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: const Text('Add vacancy'),
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
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: companyIdCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Company ID *',
                          border: OutlineInputBorder()),
                      validator: (v) =>
                          v == null || v.trim().isEmpty ? 'Required' : null,
                    ),
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
                onPressed: () => Navigator.pop(ctx, false),
                child: const Text('Cancel')),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate()) {
                  Navigator.pop(ctx, true);
                }
              },
              child: const Text('Create'),
            ),
          ],
        ),
      ),
    );

    if (confirmed != true || !mounted) return;

    final skills = skillsCtrl.text
        .split(',')
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();

    final body = <String, dynamic>{
      'title': titleCtrl.text.trim(),
      'companyId': companyIdCtrl.text.trim(),
      'status': status.name,
      if (descCtrl.text.trim().isNotEmpty) 'description': descCtrl.text.trim(),
      if (requirementsCtrl.text.trim().isNotEmpty)
        'requirements': requirementsCtrl.text.trim(),
      if (salaryFromCtrl.text.trim().isNotEmpty)
        'salaryFrom': int.tryParse(salaryFromCtrl.text.trim()),
      if (salaryToCtrl.text.trim().isNotEmpty)
        'salaryTo': int.tryParse(salaryToCtrl.text.trim()),
      if (currencyCtrl.text.trim().isNotEmpty) 'currency': currencyCtrl.text.trim(),
      if (employmentType != null) 'employmentType': employmentType!.name,
      if (workFormat != null) 'workFormat': workFormat!.name,
      if (experienceLevel != null) 'experienceLevel': experienceLevel!.name,
      if (cityCtrl.text.trim().isNotEmpty) 'city': cityCtrl.text.trim(),
      if (countryCtrl.text.trim().isNotEmpty) 'country': countryCtrl.text.trim(),
      if (skills.isNotEmpty) 'skills': skills,
      if (contactNameCtrl.text.trim().isNotEmpty)
        'contactName': contactNameCtrl.text.trim(),
      if (contactEmailCtrl.text.trim().isNotEmpty)
        'contactEmail': contactEmailCtrl.text.trim(),
      if (contactPhoneCtrl.text.trim().isNotEmpty)
        'contactPhone': contactPhoneCtrl.text.trim(),
    };

    final created = await widget.vacancyApiService.createVacancy(body);
    if (mounted) {
      if (created == null) {
        _showSnack('Failed to create vacancy');
      } else {
        await _loadPage(reset: true);
      }
    }
  }

  Future<void> _showEditDialog(Vacancy vacancy) async {
    final titleCtrl = TextEditingController(text: vacancy.title);
    final descCtrl = TextEditingController(text: vacancy.description ?? '');
    final requirementsCtrl =
        TextEditingController(text: vacancy.requirements ?? '');
    final salaryFromCtrl =
        TextEditingController(text: vacancy.salaryFrom?.toString() ?? '');
    final salaryToCtrl =
        TextEditingController(text: vacancy.salaryTo?.toString() ?? '');
    final currencyCtrl =
        TextEditingController(text: vacancy.currency ?? 'RUB');
    final cityCtrl = TextEditingController(text: vacancy.city ?? '');
    final countryCtrl = TextEditingController(text: vacancy.country ?? '');
    final contactNameCtrl =
        TextEditingController(text: vacancy.contactName ?? '');
    final contactEmailCtrl =
        TextEditingController(text: vacancy.contactEmail ?? '');
    final contactPhoneCtrl =
        TextEditingController(text: vacancy.contactPhone ?? '');
    final skillsCtrl =
        TextEditingController(text: vacancy.skills.join(', '));
    EmploymentType? employmentType = vacancy.employmentType;
    WorkFormat? workFormat = vacancy.workFormat;
    ExperienceLevel? experienceLevel = vacancy.experienceLevel;
    VacancyStatus status = vacancy.status;
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: Text('Edit "${vacancy.title}"'),
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
                onPressed: () => Navigator.pop(ctx, false),
                child: const Text('Cancel')),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate()) {
                  Navigator.pop(ctx, true);
                }
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );

    if (confirmed != true || !mounted) return;

    final skills = skillsCtrl.text
        .split(',')
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();

    final body = <String, dynamic>{
      'title': titleCtrl.text.trim(),
      'status': status.name,
      'description': descCtrl.text.trim().isEmpty ? null : descCtrl.text.trim(),
      'requirements': requirementsCtrl.text.trim().isEmpty
          ? null
          : requirementsCtrl.text.trim(),
      'salaryFrom': int.tryParse(salaryFromCtrl.text.trim()),
      'salaryTo': int.tryParse(salaryToCtrl.text.trim()),
      'currency': currencyCtrl.text.trim().isEmpty
          ? null
          : currencyCtrl.text.trim(),
      'employmentType': employmentType?.name,
      'workFormat': workFormat?.name,
      'experienceLevel': experienceLevel?.name,
      'city': cityCtrl.text.trim().isEmpty ? null : cityCtrl.text.trim(),
      'country': countryCtrl.text.trim().isEmpty ? null : countryCtrl.text.trim(),
      'skills': skills,
      'contactName': contactNameCtrl.text.trim().isEmpty
          ? null
          : contactNameCtrl.text.trim(),
      'contactEmail': contactEmailCtrl.text.trim().isEmpty
          ? null
          : contactEmailCtrl.text.trim(),
      'contactPhone': contactPhoneCtrl.text.trim().isEmpty
          ? null
          : contactPhoneCtrl.text.trim(),
    };

    final updated =
        await widget.vacancyApiService.updateVacancy(vacancy.id, body);
    if (mounted) {
      if (updated == null) {
        _showSnack('Failed to update vacancy');
      } else {
        await _loadPage(reset: true);
      }
    }
  }

  Future<void> _delete(Vacancy vacancy) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete vacancy'),
        content: Text('Delete "${vacancy.title}"?'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red, foregroundColor: Colors.white),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirmed != true) return;
    await widget.vacancyApiService.deleteVacancy(vacancy.id);
    if (mounted) await _loadPage(reset: true);
  }

  void _showSnack(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

  Color _statusColor(VacancyStatus s) => switch (s) {
        VacancyStatus.ACTIVE => Colors.green,
        VacancyStatus.DRAFT => Colors.orange,
        VacancyStatus.CLOSED => Colors.grey,
      };

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _searchController,
                  decoration: InputDecoration(
                    hintText: 'Search by title…',
                    prefixIcon: const Icon(Icons.search),
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8)),
                    contentPadding: const EdgeInsets.symmetric(vertical: 0),
                    suffixIcon: _searchController.text.isNotEmpty
                        ? IconButton(
                            icon: const Icon(Icons.clear),
                            onPressed: () {
                              _searchController.clear();
                              _loadPage(reset: true);
                            },
                          )
                        : null,
                  ),
                  onSubmitted: (_) => _loadPage(reset: true),
                ),
              ),
              const SizedBox(width: 8),
              DropdownButton<VacancyStatus>(
                value: _statusFilter,
                underline: const SizedBox(),
                items: VacancyStatus.values
                    .map((s) => DropdownMenuItem(
                        value: s, child: Text(s.displayName)))
                    .toList(),
                onChanged: (v) {
                  if (v != null) {
                    setState(() => _statusFilter = v);
                    _loadPage(reset: true);
                  }
                },
              ),
              const SizedBox(width: 8),
              FilledButton.icon(
                style: FilledButton.styleFrom(backgroundColor: Colors.indigo),
                icon: const Icon(Icons.add),
                label: const Text('Add'),
                onPressed: _showAddDialog,
              ),
            ],
          ),
        ),
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _vacancies.isEmpty
                  ? const Center(child: Text('No vacancies found'))
                  : RefreshIndicator(
                      onRefresh: () => _loadPage(reset: true),
                      child: ListView.separated(
                        controller: _scrollController,
                        itemCount:
                            _vacancies.length + (_isLoadingMore ? 1 : 0),
                        separatorBuilder: (_, __) =>
                            const Divider(height: 1),
                        itemBuilder: (_, i) {
                          if (i == _vacancies.length) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 16),
                              child:
                                  Center(child: CircularProgressIndicator()),
                            );
                          }
                          final v = _vacancies[i];
                          return ListTile(
                            leading: CircleAvatar(
                              backgroundColor: Colors.indigo.shade50,
                              child: Icon(Icons.work_outline,
                                  color: Colors.indigo.shade400),
                            ),
                            title: Text(v.title),
                            subtitle: Text([
                              if (v.company != null) v.company!.name,
                              if (v.city != null) v.city!,
                              if (v.employmentType != null)
                                v.employmentType!.displayName,
                              if (v.salaryFrom != null || v.salaryTo != null)
                                formatSalary(v.salaryFrom, v.salaryTo,
                                    v.currency ?? 'RUB'),
                            ].join(' · ')),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Container(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 8, vertical: 2),
                                  decoration: BoxDecoration(
                                    color: _statusColor(v.status)
                                        .withOpacity(0.1),
                                    borderRadius: BorderRadius.circular(12),
                                    border: Border.all(
                                        color: _statusColor(v.status)
                                            .withOpacity(0.4)),
                                  ),
                                  child: Text(
                                    v.status.displayName,
                                    style: TextStyle(
                                        fontSize: 11,
                                        color: _statusColor(v.status)),
                                  ),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.edit_outlined),
                                  tooltip: 'Edit',
                                  onPressed: () => _showEditDialog(v),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.delete_outline,
                                      color: Colors.red),
                                  tooltip: 'Delete',
                                  onPressed: () => _delete(v),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
        ),
      ],
    );
  }
}
