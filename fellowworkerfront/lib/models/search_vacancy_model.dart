/*
 * Copyright (c) 3-3/1/23, 10:18 PM
 * Created by https://github.com/alwayswanna
 */
import 'package:json_annotation/json_annotation.dart';

part '../generated/search_vacancy_model.g.dart';

@JsonSerializable()
class SearchVacancyApiModel {
  final String? city;
  final String? typeOfWork;
  final String? typeOfWorkPlacement;
  final String? keySkills;

  const SearchVacancyApiModel({
    required this.city,
    required this.typeOfWork,
    required this.typeOfWorkPlacement,
    required this.keySkills,
  });

  Map<String, dynamic> toJson() => _$SearchVacancyApiModelToJson(this);
}
