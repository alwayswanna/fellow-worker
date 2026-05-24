import 'package:flutter/material.dart';

import '../../model/user_profile.dart';
import '../../service/admin_api_service.dart';
import '../../util/dialogs.dart';
import '../../util/formatters.dart';
import '../../util/paginated_list_controller.dart';
import '../../theme/app_theme.dart';

class AdminUsersTab extends StatefulWidget {
  final AdminApiService adminApiService;

  const AdminUsersTab({super.key, required this.adminApiService});

  @override
  State<AdminUsersTab> createState() => _AdminUsersTabState();
}

class _AdminUsersTabState extends State<AdminUsersTab>
    with PaginatedListMixin<UserProfile, AdminUsersTab> {
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
  Future<(List<UserProfile>, bool)> fetchPage(int page) async {
    final result = await widget.adminApiService.getUsers(page: page, size: 20);
    return (result.content, !result.last);
  }

  Future<void> _showEditDialog(UserProfile user) async {
    final firstNameController = TextEditingController(text: user.firstName);
    final lastNameController = TextEditingController(text: user.lastName);
    final birthDateController = TextEditingController(text: user.birthDate);
    final passwordController = TextEditingController();

    Future<void> pickDate() async {
      DateTime initial;
      try {
        initial = DateTime.parse(user.birthDate);
      } catch (_) {
        initial = DateTime(2000);
      }
      final picked = await showDatePicker(
        context: context,
        initialDate: initial,
        firstDate: DateTime(1900),
        lastDate: DateTime.now(),
      );
      if (picked != null) {
        birthDateController.text = formatDate(picked);
      }
    }

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Edit ${user.login}'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: firstNameController,
                decoration: const InputDecoration(
                    labelText: 'First name', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: lastNameController,
                decoration: const InputDecoration(
                    labelText: 'Last name', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: birthDateController,
                readOnly: true,
                onTap: pickDate,
                decoration: const InputDecoration(
                  labelText: 'Birth date',
                  border: OutlineInputBorder(),
                  suffixIcon: Icon(Icons.calendar_today),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: passwordController,
                obscureText: true,
                decoration: const InputDecoration(
                    labelText: 'New password (optional)',
                    border: OutlineInputBorder()),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Save'),
          ),
        ],
      ),
    );

    if (confirmed != true || !mounted) return;

    await widget.adminApiService.updateUser(
      user.id,
      firstName: firstNameController.text.trim().isEmpty
          ? null
          : firstNameController.text.trim(),
      lastName: lastNameController.text.trim().isEmpty
          ? null
          : lastNameController.text.trim(),
      birthDate: birthDateController.text.trim().isEmpty
          ? null
          : birthDateController.text.trim(),
      password: passwordController.text.isEmpty ? null : passwordController.text,
    );
    await loadPage(reset: true);
  }

  Future<void> _delete(UserProfile user) async {
    final confirmed = await confirmDialog(context,
        title: 'Delete user', message: 'Delete "@${user.login}"?');
    if (!confirmed) return;
    await widget.adminApiService.deleteUser(user.id);
    await loadPage(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) return const Center(child: CircularProgressIndicator());

    if (items.isEmpty) return const Center(child: Text('No users found'));

    return RefreshIndicator(
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
          final user = items[i];
          final initials =
              user.firstName.isNotEmpty ? user.firstName[0].toUpperCase() : '?';
          return ListTile(
            leading: CircleAvatar(
              backgroundColor: AppColors.accent,
              child:
                  Text(initials, style: TextStyle(color: AppColors.surface)),
            ),
            title: Text(user.fullName),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('@${user.login}', style: const TextStyle(fontSize: 12)),
                if (user.role != null)
                  Text(user.role!.displayName,
                      style: TextStyle(
                          fontSize: 11, color: AppColors.primary)),
              ],
            ),
            isThreeLine: user.role != null,
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit_outlined),
                  tooltip: 'Edit',
                  onPressed: () => _showEditDialog(user),
                ),
                IconButton(
                  icon:
                      Icon(Icons.delete_outline, color: AppColors.error),
                  tooltip: 'Delete',
                  onPressed: () => _delete(user),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
