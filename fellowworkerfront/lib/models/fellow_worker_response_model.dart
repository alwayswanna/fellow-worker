/*
 * Copyright (c) 1-1/25/23, 11:37 PM
 * Created by https://github.com/alwayswanna
 */

class FellowWorkerResponseModel {
  final String message;
  final ResumeResponseModel? resumeResponse;
  final List<ResumeResponseModel>? resumes;
  final VacancyResponseApiModel? vacancyResponse;
  final List<VacancyResponseApiModel>? vacancies;

  const FellowWorkerResponseModel(
      {required this.message,
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

    return FellowWorkerResponseModel(
        message: json['message'],
        resumeResponse: ResumeResponseModel.fromJson(json['resumeResponse']),
        resumes: resumes,
        vacancyResponse:
            VacancyResponseApiModel.fromJson(json['vacancyResponse']),
        vacancies: vacancies);
  }
}

class ResumeResponseModel {
  final String resumeId;
  final String userId;
  final String firstName;
  final String middleName;
  final String lastName;
  final String birthDate;
  final String job;
  final String expectedSalary;
  final String about;
  final List<EducationResponseModel>? education;
  final List<String> professionalSkills;
  final List<WorkExperienceResponseModel>? workingHistory;
  final ContactResponseModel contact;
  final String lastUpdate;

  const ResumeResponseModel(
      {required this.resumeId,
      required this.userId,
      required this.firstName,
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
      required this.lastUpdate});

  factory ResumeResponseModel.fromJson(Map<String, dynamic> json) {
    List<EducationResponseModel> education = List.empty();
    if (json['education'] != null) {
      education = (json['education'] as List)
          .map((e) => EducationResponseModel.fromJson(e))
          .toList();
    }

    List<WorkExperienceResponseModel> workingHistory = List.empty();
    if (json['workingHistory'] != null) {
      workingHistory = (json['workingHistory'] as List)
          .map((e) => WorkExperienceResponseModel.fromJson(e))
          .toList();
    }

    return ResumeResponseModel(
        resumeId: json['resumeId'],
        userId: json['userId'],
        firstName: json['firstName'],
        middleName: json['middleName'],
        lastName: json['lastName'],
        birthDate: json['birthDate'],
        job: json['job'],
        expectedSalary: json['expectedSalary'],
        about: json['about'],
        education: education,
        professionalSkills: json['professionalSkills'],
        workingHistory: workingHistory,
        contact: ContactResponseModel.fromJson(json['contact']),
        lastUpdate: json['contact']);
  }
}

class WorkExperienceResponseModel {
  final String startTime;
  final String endTime;
  final String companyName;
  final String workingSpeciality;
  final String responsibilities;

  const WorkExperienceResponseModel(
      {required this.startTime,
      required this.endTime,
      required this.companyName,
      required this.workingSpeciality,
      required this.responsibilities});

  factory WorkExperienceResponseModel.fromJson(Map<String, dynamic> json) {
    return WorkExperienceResponseModel(
        startTime: json['startTime'],
        endTime: json['endTime'],
        companyName: json['companyName'],
        workingSpeciality: json['workingSpeciality'],
        responsibilities: json['responsibilities']);
  }
}

class ContactResponseModel {
  final String phone;
  final String email;

  const ContactResponseModel({required this.phone, required this.email});

  factory ContactResponseModel.fromJson(Map<String, dynamic> json) {
    return ContactResponseModel(phone: json['phone'], email: json['email']);
  }
}

class EducationResponseModel {
  final String startTime;
  final String endTime;
  final String educationInstitution;
  final String educationLevel;

  const EducationResponseModel(
      {required this.startTime,
      required this.endTime,
      required this.educationInstitution,
      required this.educationLevel});

  factory EducationResponseModel.fromJson(Map<String, dynamic> json) {
    return EducationResponseModel(
        startTime: json['startTime'],
        endTime: json['endTime'],
        educationInstitution: json['educationInstitution'],
        educationLevel: json['educationLevel']);
  }
}

class VacancyResponseApiModel {
  final String resumeId;
  final String vacancyName;
  final String typeOfWork;
  final String typeOfWorkPlacement;
  final String companyName;
  final String companyFullAddress;
  final List<String> keySkills;
  final String cityName;
  final List<String> workingResponsibilities;
  final List<String> companyBonuses;
  final ContactApiModel contactApiModel;
  final String lastUpdate;

  const VacancyResponseApiModel(
      {required this.resumeId,
      required this.vacancyName,
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

  factory VacancyResponseApiModel.fromJson(Map<String, dynamic> json) {
    return VacancyResponseApiModel(
        resumeId: json['resumeId'],
        vacancyName: json['vacancyName'],
        typeOfWork: json['typeOfWork'],
        typeOfWorkPlacement: json['typeOfWorkPlacement'],
        companyName: json['companyName'],
        companyFullAddress: json['companyFullAddress'],
        keySkills: json['keySkills'],
        cityName: json['cityName'],
        workingResponsibilities: json['workingResponsibilities'],
        companyBonuses: json['companyBonuses'],
        contactApiModel: ContactApiModel.fromJson(json['contactApiModel']),
        lastUpdate: json['lastUpdate']);
  }
}

class ContactApiModel {
  final String fio;
  final String phone;
  final String email;

  const ContactApiModel(
      {required this.fio, required this.phone, required this.email});

  factory ContactApiModel.fromJson(Map<String, dynamic> json) {
    return ContactApiModel(
        fio: json['fio'], phone: json['phone'], email: json['email']);
  }
}
