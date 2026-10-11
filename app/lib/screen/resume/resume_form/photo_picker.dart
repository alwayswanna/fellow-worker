import 'dart:convert';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../../theme/app_theme.dart';

typedef PhotoPicked = void Function(
    Uint8List bytes, String filename, String dataUrl);

/// Circular avatar with a camera badge; tapping opens the browser's file
/// picker and decodes the chosen image before calling [onPicked].
class PhotoPicker extends StatelessWidget {
  final Uint8List? photoBytes;
  final String? existingPhotoUrl;
  final PhotoPicked onPicked;

  const PhotoPicker({
    super.key,
    required this.photoBytes,
    required this.existingPhotoUrl,
    required this.onPicked,
  });

  // Kept in sync with the backend allowlist (resume-app's
  // ResumeAppConstant.ALLOWED_PHOTO_EXTENSIONS) — anything else is rejected
  // server-side with a 400, so there's no point offering it here.
  void _pick() {
    final input = html.FileUploadInputElement()
      ..accept = '.jpg,.jpeg,.png,.webp';
    input.click();
    input.onChange.listen((event) {
      final file = input.files?.first;
      if (file == null) return;
      final reader = html.FileReader();
      reader.readAsDataUrl(file);
      reader.onLoadEnd.listen((_) {
        final dataUrl = reader.result as String;
        final base64 = dataUrl.split(',').last;
        final bytes = base64Decode(base64);
        onPicked(bytes, file.name, dataUrl);
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    final hasPreview = photoBytes != null || existingPhotoUrl != null;

    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Center(
        child: GestureDetector(
          onTap: _pick,
          child: Stack(
            children: [
              CircleAvatar(
                radius: 48,
                backgroundColor: AppColors.tagBg,
                backgroundImage: photoBytes != null
                    ? MemoryImage(photoBytes!)
                    : (existingPhotoUrl != null
                        ? NetworkImage(existingPhotoUrl!)
                        : null),
                child: !hasPreview
                    ? Icon(Icons.person, size: 48, color: AppColors.border)
                    : null,
              ),
              Positioned(
                bottom: 0,
                right: 0,
                child: Container(
                  width: 28,
                  height: 28,
                  decoration: BoxDecoration(
                    color: AppColors.primary,
                    shape: BoxShape.circle,
                    border: Border.all(color: AppColors.surface, width: 2),
                  ),
                  child: Icon(Icons.camera_alt, size: 14, color: AppColors.surface),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
