import 'package:flutter/material.dart';

/// A styled search text field with a search icon, optional clear button,
/// and submit-on-enter support.
class SearchTextField extends StatefulWidget {
  final String hint;
  final String? initialValue;
  final ValueChanged<String> onSubmit;
  final ValueChanged<String>? onChanged;

  const SearchTextField({
    super.key,
    required this.hint,
    this.initialValue,
    required this.onSubmit,
    this.onChanged,
  });

  @override
  State<SearchTextField> createState() => _SearchTextFieldState();
}

class _SearchTextFieldState extends State<SearchTextField> {
  late final TextEditingController _ctrl;

  @override
  void initState() {
    super.initState();
    _ctrl = TextEditingController(text: widget.initialValue);
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: _ctrl,
      decoration: InputDecoration(
        hintText: widget.hint,
        prefixIcon: const Icon(Icons.search, size: 20),
        suffixIcon: _ctrl.text.isNotEmpty
            ? IconButton(
                icon: const Icon(Icons.clear, size: 18),
                onPressed: () {
                  _ctrl.clear();
                  widget.onSubmit('');
                  setState(() {});
                },
              )
            : null,
        filled: true,
        fillColor: Colors.white,
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFFDDDDDD)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Colors.indigo),
        ),
      ),
      onChanged: (v) {
        widget.onChanged?.call(v);
        setState(() {});
      },
      onSubmitted: widget.onSubmit,
      textInputAction: TextInputAction.search,
    );
  }
}
