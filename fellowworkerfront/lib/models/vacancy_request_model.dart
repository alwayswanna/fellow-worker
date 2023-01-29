/*
 * Copyright (c) 2-2/19/23, 11:28 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/vacancy_request_model.g.dart';

@JsonSerializable()
class VacancyApiModel {
  final String? vacancyId;
  final String? vacancyName;
  final String? typeOfWork;
  final String? typeOfWorkPlacement;
  final String? companyName;
  final String? companyFullAddress;
  final List<String>? keySkills;
  final String? cityName;
  final List<String>? workingResponsibilities;
  final List<String>? companyBonuses;
  final dynamic contactApiModel;

  const VacancyApiModel({required this.vacancyId,
    required this.vacancyName,
    required this.typeOfWork,
    required this.typeOfWorkPlacement,
    required this.companyName,
    required this.companyFullAddress,
    required this.keySkills,
    required this.cityName,
    required this.workingResponsibilities,
    required this.companyBonuses,
    required this.contactApiModel});

  Map<String, dynamic> toJson() => _$VacancyApiModelToJson(this);
}

@JsonSerializable()
class CompanyContactApiModel {
  final String? phone;
  final String? email;
  final String? fio;

  const CompanyContactApiModel({
    required this.phone,
    required this.email,
    required this.fio
  });

  Map<String, dynamic> toJson() => _$CompanyContactApiModelToJson(this);
}
