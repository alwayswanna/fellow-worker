import 'package:flutter/material.dart';

import '../../model/user_profile.dart';
import '../../service/admin_api_service.dart';
import '../../util/formatters.dart';

class AdminUsersTab extends StatefulWidget {
  final AdminApiService adminApiService;

  const AdminUsersTab({super.key, required this.adminApiService});

  @override
  State<AdminUsersTab> createState() => _AdminUsersTabState();
}

class _AdminUsersTabState extends State<AdminUsersTab> {
  final List<UserProfile> _users = [];
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
        _users.clear();
        _page = 0;
        _hasMore = true;
      });
    } else {
      setState(() => _isLoadingMore = true);
    }

    final result =
        await widget.adminApiService.getUsers(page: _page, size: 20);

    if (mounted) {
      setState(() {
        _users.addAll(result.content);
        _hasMore = !result.last;
        _page++;
        _isLoading = false;
        _isLoadingMore = false;
      });
    }
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
    await _loadPage(reset: true);
  }

  Future<void> _delete(UserProfile user) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete user'),
        content: Text('Delete "@${user.login}"?'),
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
    await widget.adminApiService.deleteUser(user.id);
    await _loadPage(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());

    if (_users.isEmpty) return const Center(child: Text('No users found'));

    return RefreshIndicator(
      onRefresh: () => _loadPage(reset: true),
      child: ListView.separated(
        controller: _scrollController,
        itemCount: _users.length + (_isLoadingMore ? 1 : 0),
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (_, i) {
          if (i == _users.length) {
            return const Padding(
              padding: EdgeInsets.symmetric(vertical: 16),
              child: Center(child: CircularProgressIndicator()),
            );
          }
          final user = _users[i];
          final initials =
              user.firstName.isNotEmpty ? user.firstName[0].toUpperCase() : '?';
          return ListTile(
            leading: CircleAvatar(
              backgroundColor: Colors.indigo,
              child:
                  Text(initials, style: const TextStyle(color: Colors.white)),
            ),
            title: Text(user.fullName),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('@${user.login}', style: const TextStyle(fontSize: 12)),
                if (user.role != null)
                  Text(user.role!.displayName,
                      style: const TextStyle(
                          fontSize: 11, color: Colors.indigo)),
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
                      const Icon(Icons.delete_outline, color: Colors.red),
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
