/*
 * Copyright (c) 1-3/6/23, 10:57 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/resume_request_model.g.dart';

@JsonSerializable()
class ResumeApiModel {
  final String? resumeId;
  final String? firstName;
  final String? middleName;
  final String? lastName;
  final String? birthDate;
  final String? job;
  final String? city;
  final String? expectedSalary;
  final String? about;
  final List<dynamic>? education;
  final List<String>? professionalSkills;
  final List<dynamic>? workingHistory;
  final dynamic contact;
  final String? base64Image;
  final String? extensionPostfix;

  const ResumeApiModel(
      {required this.resumeId,
      required this.firstName,
      required this.middleName,
      required this.lastName,
      required this.birthDate,
      required this.job,
      required this.expectedSalary,
      required this.city,
      required this.about,
      required this.education,
      required this.professionalSkills,
      required this.workingHistory,
      required this.contact,
      required this.base64Image,
      required this.extensionPostfix});

  Map<String, dynamic> toJson() => _$ResumeApiModelToJson(this);
}

@JsonSerializable()
class EducationApiModel {
  final String startTime;
  final String endTime;
  final String educationalInstitution;
  final String educationLevel;

  const EducationApiModel(
      {required this.startTime,
      required this.endTime,
      required this.educationalInstitution,
      required this.educationLevel});

  Map<String, dynamic> toJson() => _$EducationApiModelToJson(this);
}

@JsonSerializable()
class WorkExperienceApiModel {
  final String startTime;
  final String endTime;
  final String companyName;
  final String workingSpecialty;
  final String responsibilities;

  WorkExperienceApiModel(
      {required this.startTime,
      required this.endTime,
      required this.companyName,
      required this.workingSpecialty,
      required this.responsibilities});

  Map<String, dynamic> toJson() => _$WorkExperienceApiModelToJson(this);
}

@JsonSerializable()
class ContactResumeApiModel {
  final String phone;
  final String email;

  ContactResumeApiModel({required this.phone, required this.email});

  Map<String, dynamic> toJson() => _$ContactResumeApiModelToJson(this);
}
