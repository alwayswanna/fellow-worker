/*
 * Copyright (c) 1-3/6/23, 10:57 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/fellow_worker_response_model.g.dart';

@JsonSerializable()
class FellowWorkerResponseModel {
  final String message;
  final ResumeResponseModel? resumeResponse;
  final List<ResumeResponseModel>? resumes;
  final VacancyResponseApiModel? vacancyResponse;
  final List<VacancyResponseApiModel>? vacancies;

  const FellowWorkerResponseModel({required this.message,
    required this.resumeResponse,
    required this.resumes,
    required this.vacancyResponse,
    required this.vacancies});

  factory FellowWorkerResponseModel.fromJson(Map<String, dynamic> json) {
    List<ResumeResponseModel> resumes = List.empty();
    List<VacancyResponseApiModel> vacancies = List.empty();
    if (json['resumes'] != null) {
      resumes = (json['resumes'] as List)
          .map((e) => ResumeResponseModel.fromJson(e))
          .toList();
    }

    if (json['vacancies'] != null) {
      vacancies = (json['vacancies'] as List)
          .map((e) => VacancyResponseApiModel.fromJson(e))
          .toList();
    }

    ResumeResponseModel? resumeResponseModel;
    if (json['resumeResponse'] != null) {
      resumeResponseModel =
          ResumeResponseModel.fromJson(json['resumeResponse']);
    }

    VacancyResponseApiModel? vacancyResponseApiModel;
    if (json['vacancyResponse'] != null) {
      vacancyResponseApiModel =
          VacancyResponseApiModel.fromJson(json['vacancyResponse']);
    }

    return FellowWorkerResponseModel(
        message: json['message'],
        resumeResponse: resumeResponseModel,
        resumes: resumes,
        vacancyResponse: vacancyResponseApiModel,
        vacancies: vacancies);
  }
}

@JsonSerializable()
class ResumeResponseModel {
  final String resumeId;
  final String userId;
  final String firstName;
  final String middleName;
  final String? lastName;
  final String birthDate;
  final String job;
  final String? city;
  final String? expectedSalary;
  final String? about;
  final List<dynamic>? education;
  final List<String> professionalSkills;
  final List<dynamic>? workingHistory;
  final dynamic contact;
  final String lastUpdate;

  const ResumeResponseModel({required this.resumeId,
    required this.userId,
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
    required this.lastUpdate});

  factory ResumeResponseModel.fromJson(Map<String, dynamic> json) =>
      _$ResumeResponseModelFromJson(json);
}

@JsonSerializable()
class WorkExperienceResponseModel {
  final String startTime;
  final String endTime;
  final String companyName;
  final String workingSpeciality;
  final String responsibilities;

  const WorkExperienceResponseModel({required this.startTime,
    required this.endTime,
    required this.companyName,
    required this.workingSpeciality,
    required this.responsibilities});

  factory WorkExperienceResponseModel.fromJson(Map<String, dynamic> json) =>
      _$WorkExperienceResponseModelFromJson(json);
}

@JsonSerializable()
class ContactResponseModel {
  final String phone;
  final String email;

  const ContactResponseModel({required this.phone, required this.email});

  factory ContactResponseModel.fromJson(Map<String, dynamic> json) =>
      _$ContactResponseModelFromJson(json);
}

@JsonSerializable()
class EducationResponseModel {
  final String startTime;
  final String endTime;
  final String educationalInstitution;
  final String educationLevel;

  const EducationResponseModel({required this.startTime,
    required this.endTime,
    required this.educationalInstitution,
    required this.educationLevel});

  factory EducationResponseModel.fromJson(Map<String, dynamic> json) =>
      _$EducationResponseModelFromJson(json);
}

@JsonSerializable()
class VacancyResponseApiModel {
  final String resumeId;
  final String vacancyName;
  final String? salary;
  final String typeOfWork;
  final String typeOfWorkPlacement;
  final String companyName;
  final String companyFullAddress;
  final List<String> keySkills;
  final String cityName;
  final List<String> workingResponsibilities;
  final List<String> companyBonuses;
  final dynamic contactApiModel;
  final String lastUpdate;

  const VacancyResponseApiModel({required this.resumeId,
    required this.vacancyName,
    required this.salary,
    required this.typeOfWork,
    required this.typeOfWorkPlacement,
    required this.companyName,
    required this.companyFullAddress,
    required this.keySkills,
    required this.cityName,
    required this.workingResponsibilities,
    required this.companyBonuses,
    required this.contactApiModel,
    required this.lastUpdate});

  factory VacancyResponseApiModel.fromJson(Map<String, dynamic> json) =>
      _$VacancyResponseApiModelFromJson(json);
}

@JsonSerializable()
class ContactApiModel {
  final String fio;
  final String phone;
  final String email;

  const ContactApiModel(
      {required this.fio, required this.phone, required this.email});

  factory ContactApiModel.fromJson(Map<String, dynamic> json) =>
      _$ContactApiModelFromJson(json);
}
