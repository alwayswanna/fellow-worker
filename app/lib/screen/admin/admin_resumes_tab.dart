import 'package:flutter/material.dart';

import '../../config/app_config.dart';
import '../../model/resume.dart';
import '../../service/resume_api_service.dart';
import '../../util/paginated_list_controller.dart';
import '../../theme/app_theme.dart';

class AdminResumesTab extends StatefulWidget {
  final ResumeApiService resumeApiService;

  const AdminResumesTab({super.key, required this.resumeApiService});

  @override
  State<AdminResumesTab> createState() => _AdminResumesTabState();
}

class _AdminResumesTabState extends State<AdminResumesTab>
    with PaginatedListMixin<Resume, AdminResumesTab> {
  final TextEditingController _searchController = TextEditingController();
  static const int _pageSize = 20;

  @override
  void initState() {
    super.initState();
    initPagination();
  }

  @override
  void dispose() {
    disposePagination();
    _searchController.dispose();
    super.dispose();
  }

  @override
  Future<(List<Resume>, bool)> fetchPage(int page) async {
    final query = _searchController.text.trim();
    final results = await widget.resumeApiService.search(
      firstName: query.isNotEmpty ? query : null,
      page: page,
      size: _pageSize,
    );
    return (results, results.length >= _pageSize);
  }

  void _showDetail(Resume resume) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (_) => _ResumeDetailSheet(resume: resume),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: TextField(
            controller: _searchController,
            decoration: InputDecoration(
              hintText: 'Search by first name…',
              prefixIcon: const Icon(Icons.search),
              border:
                  OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
              contentPadding: const EdgeInsets.symmetric(vertical: 0),
              suffixIcon: _searchController.text.isNotEmpty
                  ? IconButton(
                      icon: const Icon(Icons.clear),
                      onPressed: () {
                        _searchController.clear();
                        loadPage(reset: true);
                      },
                    )
                  : null,
            ),
            onSubmitted: (_) => loadPage(reset: true),
          ),
        ),
        Expanded(
          child: isLoading
              ? const Center(child: CircularProgressIndicator())
              : items.isEmpty
                  ? const Center(child: Text('No resumes found'))
                  : RefreshIndicator(
                      onRefresh: () => loadPage(reset: true),
                      child: ListView.separated(
                        controller: scrollController,
                        itemCount:
                            items.length + (isLoadingMore ? 1 : 0),
                        separatorBuilder: (_, __) =>
                            const Divider(height: 1),
                        itemBuilder: (_, i) {
                          if (i == items.length) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 16),
                              child:
                                  Center(child: CircularProgressIndicator()),
                            );
                          }
                          final r = items[i];
                          return ListTile(
                            onTap: () => _showDetail(r),
                            leading: CircleAvatar(
                              backgroundImage: r.photoUrl != null
                                  ? NetworkImage(
                                      '${AppConfig.photoBaseUrl}${r.photoUrl}')
                                  : null,
                              backgroundColor: AppColors.tagBg,
                              child: r.photoUrl == null
                                  ? Text(
                                      r.firstName.isNotEmpty
                                          ? r.firstName[0].toUpperCase()
                                          : '?',
                                      style: TextStyle(
                                          color: AppColors.primary),
                                    )
                                  : null,
                            ),
                            title: Text(r.fullName),
                            subtitle: Text([
                              if (r.desiredPosition != null)
                                r.desiredPosition!,
                              if (r.skills.isNotEmpty)
                                r.skills.take(3).join(', '),
                            ].join(' · ')),
                            trailing: const Icon(Icons.chevron_right),
                          );
                        },
                      ),
                    ),
        ),
      ],
    );
  }
}

class _ResumeDetailSheet extends StatelessWidget {
  final Resume resume;

  const _ResumeDetailSheet({required this.resume});

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.75,
      maxChildSize: 0.95,
      minChildSize: 0.4,
      expand: false,
      builder: (_, controller) => Column(
        children: [
          Container(
            width: 40,
            height: 4,
            margin: const EdgeInsets.symmetric(vertical: 10),
            decoration: BoxDecoration(
              color: AppColors.border,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          Expanded(
            child: ListView(
              controller: controller,
              padding:
                  const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
              children: [
                // Header
                Row(
                  children: [
                    CircleAvatar(
                      radius: 32,
                      backgroundImage: resume.photoUrl != null
                          ? NetworkImage(
                              '${AppConfig.photoBaseUrl}${resume.photoUrl}')
                          : null,
                      backgroundColor: AppColors.tagBg,
                      child: resume.photoUrl == null
                          ? Text(
                              resume.firstName.isNotEmpty
                                  ? resume.firstName[0].toUpperCase()
                                  : '?',
                              style: TextStyle(
                                  fontSize: 24, color: AppColors.primary),
                            )
                          : null,
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(resume.fullName,
                              style: Theme.of(context).textTheme.titleLarge),
                          if (resume.desiredPosition != null)
                            Text(resume.desiredPosition!,
                                style: TextStyle(color: AppColors.primary)),
                          Text(resume.email,
                              style: TextStyle(color: AppColors.textSecondary)),
                          if (resume.phone != null)
                            Text(resume.phone!,
                                style: TextStyle(color: AppColors.textSecondary)),
                        ],
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                if (resume.summary != null) ...[
                  _sectionTitle('Summary'),
                  Text(resume.summary!),
                  const SizedBox(height: 16),
                ],

                if (resume.skills.isNotEmpty) ...[
                  _sectionTitle('Skills'),
                  Wrap(
                    spacing: 6,
                    runSpacing: 4,
                    children: resume.skills
                        .map((s) => Chip(
                              label: Text(s, style: const TextStyle(fontSize: 12)),
                              visualDensity: VisualDensity.compact,
                            ))
                        .toList(),
                  ),
                  const SizedBox(height: 16),
                ],

                if (resume.experience.isNotEmpty) ...[
                  _sectionTitle('Experience'),
                  ...resume.experience.map((e) => _experienceTile(e)),
                  const SizedBox(height: 16),
                ],

                if (resume.education.isNotEmpty) ...[
                  _sectionTitle('Education'),
                  ...resume.education.map((e) => _educationTile(e)),
                  const SizedBox(height: 16),
                ],

                if (resume.links.isNotEmpty) ...[
                  _sectionTitle('Links'),
                  ...resume.links.map(
                    (l) => Padding(
                      padding: const EdgeInsets.symmetric(vertical: 2),
                      child: Text(l,
                          style: TextStyle(color: AppColors.primary)),
                    ),
                  ),
                  const SizedBox(height: 16),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _sectionTitle(String title) => Padding(
        padding: const EdgeInsets.only(bottom: 8),
        child: Text(title,
            style: const TextStyle(
                fontWeight: FontWeight.w700, fontSize: 15)),
      );

  Widget _experienceTile(WorkExperience e) => Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${e.position} @ ${e.company}',
                style: const TextStyle(fontWeight: FontWeight.w600)),
            if (e.startDate != null || e.endDate != null)
              Text(
                [e.startDate, e.endDate ?? 'present']
                    .where((d) => d != null)
                    .join(' – '),
                style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
              ),
            if (e.description != null)
              Text(e.description!,
                  style: const TextStyle(fontSize: 13)),
          ],
        ),
      );

  Widget _educationTile(Education e) => Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(e.institution,
                style: const TextStyle(fontWeight: FontWeight.w600)),
            Text('${e.degree}, ${e.fieldOfStudy}',
                style: const TextStyle(fontSize: 13)),
            if (e.startDate != null || e.endDate != null)
              Text(
                [e.startDate, e.endDate ?? 'present']
                    .where((d) => d != null)
                    .join(' – '),
                style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
              ),
          ],
        ),
      );
}
