import 'package:flutter/material.dart';

import '../../model/user_profile.dart';
import '../../service/admin_api_service.dart';

class AdminRolesTab extends StatefulWidget {
  final AdminApiService adminApiService;

  const AdminRolesTab({super.key, required this.adminApiService});

  @override
  State<AdminRolesTab> createState() => _AdminRolesTabState();
}

class _AdminRolesTabState extends State<AdminRolesTab> {
  final List<UserRole> _roles = [];
  final ScrollController _scrollController = ScrollController();
  bool _isLoading = false;
  bool _isLoadingMore = false;
  bool _hasMore = true;
  int _page = 0;

  @override
  void initState() {
    super.initState();
    _loadPage(reset: true);
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
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
        _roles.clear();
        _page = 0;
        _hasMore = true;
      });
    } else {
      setState(() => _isLoadingMore = true);
    }

    final result =
        await widget.adminApiService.getRoles(page: _page, size: 20);

    if (mounted) {
      setState(() {
        _roles.addAll(result.content);
        _hasMore = !result.last;
        _page++;
        _isLoading = false;
        _isLoadingMore = false;
      });
    }
  }

  Future<void> _showRoleDialog({UserRole? role}) async {
    final codeController = TextEditingController(text: role?.code ?? '');
    final nameController = TextEditingController(text: role?.displayName ?? '');
    bool selectable = role?.selectable ?? true;
    final formKey = GlobalKey<FormState>();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: Text(role == null ? 'Create role' : 'Edit role'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: codeController,
                  decoration: const InputDecoration(
                      labelText: 'Code', border: OutlineInputBorder()),
                  validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Required' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: nameController,
                  decoration: const InputDecoration(
                      labelText: 'Display name', border: OutlineInputBorder()),
                  validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Required' : null,
                ),
                const SizedBox(height: 4),
                SwitchListTile(
                  contentPadding: EdgeInsets.zero,
                  title: const Text('Selectable by users'),
                  subtitle: const Text(
                    'Show this role in the registration form',
                    style: TextStyle(fontSize: 12),
                  ),
                  value: selectable,
                  onChanged: (v) => setDialogState(() => selectable = v),
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
                onPressed: () => Navigator.pop(ctx, false),
                child: const Text('Cancel')),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate()) Navigator.pop(ctx, true);
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );

    if (confirmed != true || !mounted) return;

    if (role == null) {
      await widget.adminApiService.createRole(
        code: codeController.text.trim(),
        displayName: nameController.text.trim(),
        selectable: selectable,
      );
    } else {
      await widget.adminApiService.updateRole(
        role.id,
        code: codeController.text.trim(),
        displayName: nameController.text.trim(),
        selectable: selectable,
      );
    }
    await _loadPage(reset: true);
  }

  Future<void> _delete(UserRole role) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete role'),
        content: Text('Delete "${role.displayName}"?'),
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
    await widget.adminApiService.deleteRole(role.id);
    await _loadPage(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: Align(
            alignment: Alignment.centerRight,
            child: ElevatedButton.icon(
              onPressed: () => _showRoleDialog(),
              icon: const Icon(Icons.add),
              label: const Text('New role'),
              style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.indigo, foregroundColor: Colors.white),
            ),
          ),
        ),
        Expanded(
          child: _roles.isEmpty
              ? const Center(child: Text('No roles found'))
              : RefreshIndicator(
                  onRefresh: () => _loadPage(reset: true),
                  child: ListView.separated(
                    controller: _scrollController,
                    itemCount: _roles.length + (_isLoadingMore ? 1 : 0),
                    separatorBuilder: (_, __) => const Divider(height: 1),
                    itemBuilder: (_, i) {
                      if (i == _roles.length) {
                        return const Padding(
                          padding: EdgeInsets.symmetric(vertical: 16),
                          child: Center(child: CircularProgressIndicator()),
                        );
                      }
                      final role = _roles[i];
                      return ListTile(
                        leading: const CircleAvatar(
                          backgroundColor: Colors.indigo,
                          child: Icon(Icons.shield_outlined,
                              color: Colors.white, size: 20),
                        ),
                        title: Row(
                          children: [
                            Text(role.displayName),
                            const SizedBox(width: 8),
                            Chip(
                              label: Text(
                                role.selectable ? 'Selectable' : 'Hidden',
                                style: const TextStyle(fontSize: 11),
                              ),
                              padding: EdgeInsets.zero,
                              visualDensity: VisualDensity.compact,
                              backgroundColor: role.selectable
                                  ? Colors.green.shade100
                                  : Colors.grey.shade200,
                              labelStyle: TextStyle(
                                color: role.selectable
                                    ? Colors.green.shade800
                                    : Colors.grey.shade600,
                              ),
                            ),
                          ],
                        ),
                        subtitle: Text(role.code,
                            style: const TextStyle(
                                fontFamily: 'monospace', fontSize: 12)),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            IconButton(
                              icon: const Icon(Icons.edit_outlined),
                              tooltip: 'Edit',
                              onPressed: () => _showRoleDialog(role: role),
                            ),
                            IconButton(
                              icon: const Icon(Icons.delete_outline,
                                  color: Colors.red),
                              tooltip: 'Delete',
                              onPressed: () => _delete(role),
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
