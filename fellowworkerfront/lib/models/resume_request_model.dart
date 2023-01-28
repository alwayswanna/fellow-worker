/*
 * Copyright (c) 1-1/29/23, 12:12 AM
 * Created by https://github.com/alwayswanna
 */

class ResumeApiModel {
  final String firstName;
  final String middleName;
  final String lastName;
  final String birthDate;
  final String job;
  final String expectedSalary;
  final String about;
  final List<EducationApiModel> education;
  final List<String> professionalSkills;
  final List<WorkExperienceApiModel> workingHistory;
  final ContactResumeApiModel contact;
  final String base64Image;
  final String extensionPostfix;

  const ResumeApiModel(
      {required this.firstName,
      required this.middleName,
      required this.lastName,
      required this.birthDate,
      required this.job,
      required this.expectedSalary,
      required this.about,
      required this.education,
      required this.professionalSkills,
      required this.workingHistory,
      required this.contact,
      required this.base64Image,
      required this.extensionPostfix});

  Map<String, dynamic> toJson() {
    return {
      'firstName': firstName,
      'middleName': middleName,
      'lastName': lastName,
      'birthDate': birthDate,
      'job': job,
      'expectedSalary': expectedSalary,
      'about': about,
      'education': education,
      'professionalSkills': professionalSkills,
      'workingHistory': workingHistory,
      'contact': contact,
      'base64Image': base64Image,
      'extensionPostfix': extensionPostfix
    };
  }
}

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

  Map<String, dynamic> toJson() {
    return {
      'startTime': startTime,
      'endTime': endTime,
      'educationalInstitution': educationalInstitution,
      'educationLevel': educationLevel
    };
  }
}

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

  Map<String, dynamic> toJson() {
    return {
      'startTime': startTime,
      'endTime': endTime,
      'companyName': companyName,
      'workingSpecialty': workingSpecialty,
      'responsibilities': responsibilities
    };
  }
}

class ContactResumeApiModel {
  final String phone;
  final String email;

  ContactResumeApiModel({required this.phone, required this.email});

  Map<String, dynamic> toJson() {
    return {'phone': phone, 'email': email};
  }
}
