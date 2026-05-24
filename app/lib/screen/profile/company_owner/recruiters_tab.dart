import 'package:flutter/material.dart';

import '../../../model/user_profile.dart';
import '../../../model/vacancy.dart';
import '../../../service/user_api_service.dart';
import '../../../service/vacancy_api_service.dart';
import '../../../theme/app_theme.dart';
import '../../../util/dialogs.dart';

class RecruitersTab extends StatefulWidget {
  final Company? company;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const RecruitersTab({
    super.key,
    required this.company,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<RecruitersTab> createState() => _RecruitersTabState();
}

class _RecruitersTabState extends State<RecruitersTab> {
  List<RecruiterEntry> _recruiters = [];
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
      return RecruiterEntry(recruiter: r, user: user);
    }));
    if (mounted) setState(() { _recruiters = entries; _isLoading = false; });
  }

  Future<void> _showAdd() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => AddRecruiterDialog(
        companyId: widget.company!.id,
        vacancyApiService: widget.vacancyApiService,
        userApiService: widget.userApiService,
      ),
    );
    if (ok == true) _load();
  }

  Future<void> _remove(CompanyRecruiter r) async {
    final ok = await confirmDialog(
      context,
      title: 'Remove recruiter?',
      message: 'This will revoke their recruiter access.',
      confirmLabel: 'Remove',
    );
    if (ok) {
      await widget.vacancyApiService.removeRecruiter(widget.company!.id, r.accountId);
      _load();
    }
  }

  @override
  Widget build(BuildContext context) {
    if (widget.company == null) {
      return Center(child: Padding(padding: EdgeInsets.all(32),
          child: Text('Create your company first to manage recruiters.',
              textAlign: TextAlign.center, style: TextStyle(color: AppColors.textSecondary))));
    }
    return Column(children: [
      Container(
        color: AppColors.surface,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          const Text('Recruiters', style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
          FilledButton.icon(
            onPressed: _showAdd,
            icon: const Icon(Icons.person_add_outlined, size: 16),
            label: const Text('Add recruiter'),
            style: FilledButton.styleFrom(backgroundColor: AppColors.accent,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          ),
        ]),
      ),
      Divider(height: 1, color: AppColors.border),
      Expanded(
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _recruiters.isEmpty
                ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(Icons.people_outline, size: 48, color: AppColors.border),
                    const SizedBox(height: 12),
                    Text('No recruiters yet',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600,
                            color: AppColors.text)),
                  ]))
                : ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _recruiters.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (_, i) {
                      final e = _recruiters[i];
                      final u = e.user;
                      return Material(
                        color: AppColors.surface,
                        borderRadius: BorderRadius.circular(10),
                        child: Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                          child: Row(children: [
                            CircleAvatar(
                              radius: 20, backgroundColor: AppColors.tagBg,
                              child: Text(
                                u != null && u.firstName.isNotEmpty
                                    ? u.firstName[0].toUpperCase() : '?',
                                style: TextStyle(fontWeight: FontWeight.bold,
                                    color: AppColors.primary),
                              ),
                            ),
                            const SizedBox(width: 12),
                            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                              Text(u != null ? u.fullName : e.recruiter.accountId,
                                  style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                              if (u != null)
                                Text('@${u.login}',
                                    style: TextStyle(fontSize: 12, color: AppColors.textSecondary)),
                            ])),
                            IconButton(
                              icon: Icon(Icons.person_remove_outlined,
                                  size: 20, color: AppColors.error),
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

class RecruiterEntry {
  final CompanyRecruiter recruiter;
  final UserShort? user;
  RecruiterEntry({required this.recruiter, required this.user});
}

// ── Add recruiter dialog ───────────────────────────────────────────────────────

class AddRecruiterDialog extends StatefulWidget {
  final String companyId;
  final VacancyApiService vacancyApiService;
  final UserApiService userApiService;

  const AddRecruiterDialog({
    super.key,
    required this.companyId,
    required this.vacancyApiService,
    required this.userApiService,
  });

  @override
  State<AddRecruiterDialog> createState() => _AddRecruiterDialogState();
}

class _AddRecruiterDialogState extends State<AddRecruiterDialog> {
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
              decoration: BoxDecoration(color: AppColors.errorBg,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: AppColors.error.withAlpha(80))),
              child: Text(_error!, style: TextStyle(color: AppColors.error, fontSize: 13)),
            ),
            const SizedBox(height: 12),
          ],
          TextField(
            controller: _searchCtrl,
            decoration: InputDecoration(
              hintText: 'Search by name or login...',
              prefixIcon: Icon(Icons.search, color: AppColors.textSecondary),
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
            Padding(padding: EdgeInsets.all(16),
                child: Text('No users found', style: TextStyle(color: AppColors.textSecondary)))
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
                      radius: 16, backgroundColor: AppColors.tagBg,
                      child: Text(
                        u.firstName.isNotEmpty ? u.firstName[0].toUpperCase() : '?',
                        style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold,
                            color: AppColors.primary),
                      ),
                    ),
                    title: Text(u.fullName, style: const TextStyle(fontSize: 14)),
                    subtitle: Text('@${u.login}', style: const TextStyle(fontSize: 12)),
                    selected: sel,
                    selectedTileColor: AppColors.tagBg,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    onTap: () => setState(() => _selected = u),
                    trailing: sel ? Icon(Icons.check_circle, color: AppColors.primary) : null,
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
          style: FilledButton.styleFrom(backgroundColor: AppColors.accent),
          child: _isAdding
              ? SizedBox(width: 16, height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.surface))
              : const Text('Add'),
        ),
      ],
    );
  }
}
