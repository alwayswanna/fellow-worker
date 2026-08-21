import 'package:flutter/material.dart';

import '../../../model/resume.dart';

class ExpEntry {
  final TextEditingController company;
  final TextEditingController position;
  final TextEditingController startDate;
  final TextEditingController endDate;
  final TextEditingController description;

  ExpEntry({
    required this.company,
    required this.position,
    required this.startDate,
    required this.endDate,
    required this.description,
  });

  factory ExpEntry.empty() => ExpEntry(
        company: TextEditingController(),
        position: TextEditingController(),
        startDate: TextEditingController(),
        endDate: TextEditingController(),
        description: TextEditingController(),
      );

  factory ExpEntry.fromModel(WorkExperience m) => ExpEntry(
        company: TextEditingController(text: m.company),
        position: TextEditingController(text: m.position),
        startDate: TextEditingController(text: m.startDate ?? ''),
        endDate: TextEditingController(text: m.endDate ?? ''),
        description: TextEditingController(text: m.description ?? ''),
      );

  Map<String, dynamic> toJson() => {
        'company': company.text.trim(),
        'position': position.text.trim(),
        if (startDate.text.isNotEmpty) 'startDate': startDate.text,
        if (endDate.text.isNotEmpty) 'endDate': endDate.text,
        if (description.text.trim().isNotEmpty)
          'description': description.text.trim(),
      };

  void dispose() {
    company.dispose();
    position.dispose();
    startDate.dispose();
    endDate.dispose();
    description.dispose();
  }
}

class EduEntry {
  final TextEditingController institution;
  final TextEditingController degree;
  final TextEditingController fieldOfStudy;
  final TextEditingController startDate;
  final TextEditingController endDate;

  EduEntry({
    required this.institution,
    required this.degree,
    required this.fieldOfStudy,
    required this.startDate,
    required this.endDate,
  });

  factory EduEntry.empty() => EduEntry(
        institution: TextEditingController(),
        degree: TextEditingController(),
        fieldOfStudy: TextEditingController(),
        startDate: TextEditingController(),
        endDate: TextEditingController(),
      );

  factory EduEntry.fromModel(Education m) => EduEntry(
        institution: TextEditingController(text: m.institution),
        degree: TextEditingController(text: m.degree),
        fieldOfStudy: TextEditingController(text: m.fieldOfStudy),
        startDate: TextEditingController(text: m.startDate ?? ''),
        endDate: TextEditingController(text: m.endDate ?? ''),
      );

  Map<String, dynamic> toJson() => {
        'institution': institution.text.trim(),
        'degree': degree.text.trim(),
        'fieldOfStudy': fieldOfStudy.text.trim(),
        if (startDate.text.isNotEmpty) 'startDate': startDate.text,
        if (endDate.text.isNotEmpty) 'endDate': endDate.text,
      };

  void dispose() {
    institution.dispose();
    degree.dispose();
    fieldOfStudy.dispose();
    startDate.dispose();
    endDate.dispose();
  }
}
