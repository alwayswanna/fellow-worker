import 'package:flutter/material.dart';

import '../../../theme/app_theme.dart';
import 'form_widgets.dart';

class LinksSection extends StatelessWidget {
  final List<TextEditingController> links;
  final ValueChanged<int> onRemove;
  final VoidCallback onAdd;

  const LinksSection({
    super.key,
    required this.links,
    required this.onRemove,
    required this.onAdd,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...links.asMap().entries.map((e) => Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: e.value,
                      keyboardType: TextInputType.url,
                      decoration: InputDecoration(
                        hintText: 'https://linkedin.com/in/...',
                        hintStyle:
                            TextStyle(color: AppColors.textSecondary),
                        prefixIcon: Icon(Icons.link,
                            size: 18, color: AppColors.textSecondary),
                        filled: true,
                        fillColor: AppColors.bg,
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide:
                                BorderSide(color: AppColors.border)),
                        enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide:
                                BorderSide(color: AppColors.border)),
                        focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide: BorderSide(
                                color: AppColors.primary, width: 1.5)),
                        isDense: true,
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: 14, vertical: 14),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  GestureDetector(
                    onTap: () => onRemove(e.key),
                    child: Icon(Icons.remove_circle_outline,
                        color: AppColors.error, size: 22),
                  ),
                ],
              ),
            )),
        AddEntryButton(label: 'Add link', onTap: onAdd),
      ],
    );
  }
}
