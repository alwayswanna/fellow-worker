import 'package:flutter/material.dart';

/// Infinite-scroll pagination state machine shared by every "list of pages
/// from a search endpoint" screen (the admin tabs). Mix into a [State],
/// implement [fetchPage], and call [initPagination]/[disposePagination]
/// from `initState`/`dispose`.
mixin PaginatedListMixin<T, W extends StatefulWidget> on State<W> {
  final List<T> items = [];
  final ScrollController scrollController = ScrollController();
  bool isLoading = false;
  bool isLoadingMore = false;
  bool hasMore = true;
  int page = 0;

  /// Fetches zero-indexed [page] and reports whether a further page exists.
  Future<(List<T> results, bool hasMore)> fetchPage(int page);

  void initPagination() {
    loadPage(reset: true);
    scrollController.addListener(_onScroll);
  }

  void disposePagination() {
    scrollController.dispose();
  }

  void _onScroll() {
    if (scrollController.position.pixels >=
            scrollController.position.maxScrollExtent - 200 &&
        !isLoadingMore &&
        hasMore) {
      loadPage();
    }
  }

  Future<void> loadPage({bool reset = false}) async {
    if (reset) {
      setState(() {
        isLoading = true;
        items.clear();
        page = 0;
        hasMore = true;
      });
    } else {
      setState(() => isLoadingMore = true);
    }

    final (results, more) = await fetchPage(page);

    if (mounted) {
      setState(() {
        items.addAll(results);
        hasMore = more;
        page++;
        isLoading = false;
        isLoadingMore = false;
      });
    }
  }
}
