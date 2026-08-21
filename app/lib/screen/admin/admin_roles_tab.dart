import 'package:flutter/material.dart';

import '../../model/user_profile.dart';
import '../../service/admin_api_service.dart';
import '../../util/dialogs.dart';
import '../../util/paginated_list_controller.dart';
import '../../theme/app_theme.dart';

class AdminRolesTab extends StatefulWidget {
  final AdminApiService adminApiService;

  const AdminRolesTab({super.key, required this.adminApiService});

  @override
  State<AdminRolesTab> createState() => _AdminRolesTabState();
}

class _AdminRolesTabState extends State<AdminRolesTab>
    with PaginatedListMixin<UserRole, AdminRolesTab> {
  @override
  void initState() {
    super.initState();
    initPagination();
  }

  @override
  void dispose() {
    disposePagination();
    super.dispose();
  }

  @override
  Future<(List<UserRole>, bool)> fetchPage(int page) async {
    final result = await widget.adminApiService.getRoles(page: page, size: 20);
    return (result.content, !result.last);
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
    await loadPage(reset: true);
  }

  Future<void> _delete(UserRole role) async {
    final confirmed = await confirmDialog(context,
        title: 'Delete role', message: 'Delete "${role.displayName}"?');
    if (!confirmed) return;
    await widget.adminApiService.deleteRole(role.id);
    await loadPage(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) return const Center(child: CircularProgressIndicator());

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
                  backgroundColor: AppColors.accent, foregroundColor: AppColors.surface),
            ),
          ),
        ),
        Expanded(
          child: items.isEmpty
              ? const Center(child: Text('No roles found'))
              : RefreshIndicator(
                  onRefresh: () => loadPage(reset: true),
                  child: ListView.separated(
                    controller: scrollController,
                    itemCount: items.length + (isLoadingMore ? 1 : 0),
                    separatorBuilder: (_, __) => const Divider(height: 1),
                    itemBuilder: (_, i) {
                      if (i == items.length) {
                        return const Padding(
                          padding: EdgeInsets.symmetric(vertical: 16),
                          child: Center(child: CircularProgressIndicator()),
                        );
                      }
                      final role = items[i];
                      return ListTile(
                        leading: CircleAvatar(
                          backgroundColor: AppColors.accent,
                          child: Icon(Icons.shield_outlined,
                              color: AppColors.surface, size: 20),
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
                                  ? AppColors.successBg
                                  : AppColors.border,
                              labelStyle: TextStyle(
                                color: role.selectable
                                    ? AppColors.success
                                    : AppColors.textSecondary,
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
                              icon: Icon(Icons.delete_outline,
                                  color: AppColors.error),
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
