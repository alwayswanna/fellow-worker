// Shared formatting utilities used across the app.

/// Formats a [DateTime] as `yyyy-MM-dd`.
String formatDate(DateTime date) {
  final m = date.month.toString().padLeft(2, '0');
  final d = date.day.toString().padLeft(2, '0');
  return '${date.year}-$m-$d';
}

/// Formats an integer with thin-space thousands separators (e.g. 1 000 000).
String formatNumber(int n) {
  final s = n.toString();
  final buf = StringBuffer();
  for (var i = 0; i < s.length; i++) {
    if (i > 0 && (s.length - i) % 3 == 0) buf.write('\u202F');
    buf.write(s[i]);
  }
  return buf.toString();
}

/// Formats a salary range as a human-readable string.
/// Returns null if both [from] and [to] are null.
String? formatSalary(int? from, int? to, String? currency) {
  if (from == null && to == null) return null;
  final c = currency?.isNotEmpty == true ? ' ${currency!}' : '';
  if (from != null && to != null) {
    return '${formatNumber(from)}–${formatNumber(to)}$c';
  }
  if (from != null) return 'from ${formatNumber(from)}$c';
  return 'up to ${formatNumber(to!)}$c';
}
