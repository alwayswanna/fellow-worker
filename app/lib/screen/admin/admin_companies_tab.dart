import 'package:flutter/material.dart';

import '../../model/vacancy.dart';
import '../../service/vacancy_api_service.dart';

class AdminCompaniesTab extends StatefulWidget {
  final VacancyApiService vacancyApiService;

  const AdminCompaniesTab({super.key, required this.vacancyApiService});

  @override
  State<AdminCompaniesTab> createState() => _AdminCompaniesTabState();
}

class _AdminCompaniesTabState extends State<AdminCompaniesTab> {
  final List<Company> _companies = [];
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _searchController = TextEditingController();
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
        _companies.clear();
        _page = 0;
        _hasMore = true;
      });
    } else {
      setState(() => _isLoadingMore = true);
    }

    final results = await widget.vacancyApiService.searchCompanies(
      name: _searchController.text.trim().isEmpty
          ? null
          : _searchController.text.trim(),
      page: _page,
    );

    if (mounted) {
      setState(() {
        _companies.addAll(results);
        _hasMore = results.length >= _pageSize;
        _page++;
        _isLoading = false;
        _isLoadingMore = false;
      });
    }
  }

  Future<void> _showAddDialog() async {
    final nameCtrl = TextEditingController();
    final descCtrl = TextEditingController();
    final websiteCtrl = TextEditingController();
    final industryCtrl = TextEditingController();
    final cityCtrl = TextEditingController();
    final countryCtrl = TextEditingController();
    CompanySize? selectedSize;
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: const Text('Add company'),
          content: SizedBox(
            width: 440,
            child: SingleChildScrollView(
              child: Form(
                key: formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextFormField(
                      controller: nameCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Name *', border: OutlineInputBorder()),
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
                      controller: websiteCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Website', border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: industryCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Industry', border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    DropdownButtonFormField<CompanySize>(
                      value: selectedSize,
                      decoration: const InputDecoration(
                          labelText: 'Size', border: OutlineInputBorder()),
                      items: CompanySize.values
                          .map((s) => DropdownMenuItem(
                              value: s, child: Text(s.displayName)))
                          .toList(),
                      onChanged: (v) => setDialogState(() => selectedSize = v),
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

    final body = <String, dynamic>{
      'name': nameCtrl.text.trim(),
      if (descCtrl.text.trim().isNotEmpty) 'description': descCtrl.text.trim(),
      if (websiteCtrl.text.trim().isNotEmpty) 'website': websiteCtrl.text.trim(),
      if (industryCtrl.text.trim().isNotEmpty)
        'industry': industryCtrl.text.trim(),
      if (selectedSize != null) 'size': selectedSize!.name,
      if (cityCtrl.text.trim().isNotEmpty) 'city': cityCtrl.text.trim(),
      if (countryCtrl.text.trim().isNotEmpty) 'country': countryCtrl.text.trim(),
    };

    final created = await widget.vacancyApiService.createCompany(body);
    if (mounted) {
      if (created == null) {
        _showSnack('Failed to create company');
      } else {
        await _loadPage(reset: true);
      }
    }
  }

  Future<void> _showEditDialog(Company company) async {
    final nameCtrl = TextEditingController(text: company.name);
    final descCtrl = TextEditingController(text: company.description ?? '');
    final websiteCtrl = TextEditingController(text: company.website ?? '');
    final industryCtrl = TextEditingController(text: company.industry ?? '');
    final cityCtrl = TextEditingController(text: company.city ?? '');
    final countryCtrl = TextEditingController(text: company.country ?? '');
    CompanySize? selectedSize = company.size;
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: Text('Edit "${company.name}"'),
          content: SizedBox(
            width: 440,
            child: SingleChildScrollView(
              child: Form(
                key: formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextFormField(
                      controller: nameCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Name *', border: OutlineInputBorder()),
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
                      controller: websiteCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Website', border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: industryCtrl,
                      decoration: const InputDecoration(
                          labelText: 'Industry', border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    DropdownButtonFormField<CompanySize>(
                      value: selectedSize,
                      decoration: const InputDecoration(
                          labelText: 'Size', border: OutlineInputBorder()),
                      items: CompanySize.values
                          .map((s) => DropdownMenuItem(
                              value: s, child: Text(s.displayName)))
                          .toList(),
                      onChanged: (v) => setDialogState(() => selectedSize = v),
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

    final body = <String, dynamic>{
      'name': nameCtrl.text.trim(),
      'description': descCtrl.text.trim().isEmpty ? null : descCtrl.text.trim(),
      'website': websiteCtrl.text.trim().isEmpty ? null : websiteCtrl.text.trim(),
      'industry': industryCtrl.text.trim().isEmpty ? null : industryCtrl.text.trim(),
      'size': selectedSize?.name,
      'city': cityCtrl.text.trim().isEmpty ? null : cityCtrl.text.trim(),
      'country': countryCtrl.text.trim().isEmpty ? null : countryCtrl.text.trim(),
    };

    final updated = await widget.vacancyApiService.updateCompany(company.id, body);
    if (mounted) {
      if (updated == null) {
        _showSnack('Failed to update company');
      } else {
        await _loadPage(reset: true);
      }
    }
  }

  Future<void> _delete(Company company) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete company'),
        content: Text('Delete "${company.name}"?'),
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
    await widget.vacancyApiService.deleteCompany(company.id);
    if (mounted) await _loadPage(reset: true);
  }

  void _showSnack(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

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
                    hintText: 'Search by name…',
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
              : _companies.isEmpty
                  ? const Center(child: Text('No companies found'))
                  : RefreshIndicator(
                      onRefresh: () => _loadPage(reset: true),
                      child: ListView.separated(
                        controller: _scrollController,
                        itemCount:
                            _companies.length + (_isLoadingMore ? 1 : 0),
                        separatorBuilder: (_, __) =>
                            const Divider(height: 1),
                        itemBuilder: (_, i) {
                          if (i == _companies.length) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 16),
                              child:
                                  Center(child: CircularProgressIndicator()),
                            );
                          }
                          final company = _companies[i];
                          return ListTile(
                            leading: CircleAvatar(
                              backgroundColor: Colors.indigo.shade50,
                              child: Text(
                                company.name.isNotEmpty
                                    ? company.name[0].toUpperCase()
                                    : '?',
                                style:
                                    const TextStyle(color: Colors.indigo),
                              ),
                            ),
                            title: Text(company.name),
                            subtitle: Text([
                              if (company.industry != null) company.industry!,
                              if (company.city != null) company.city!,
                              if (company.size != null)
                                company.size!.displayName,
                            ].join(' · ')),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                IconButton(
                                  icon: const Icon(Icons.edit_outlined),
                                  tooltip: 'Edit',
                                  onPressed: () => _showEditDialog(company),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.delete_outline,
                                      color: Colors.red),
                                  tooltip: 'Delete',
                                  onPressed: () => _delete(company),
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
