/*
 * Copyright (c) 3-3/7/23, 10:25 PM
 * Created by https://github.com/alwayswanna
 */
import 'package:json_annotation/json_annotation.dart';

part '../generated/search_resume_model.g.dart';

@JsonSerializable()
class SearchResumeApiModel {
  final String? city;
  final String? keySkills;
  final String? job;
  final String? salary;

  const SearchResumeApiModel({
    required this.city,
    required this.keySkills,
    required this.job,
    required this.salary,
  });

  Map<String, dynamic> toJson() => _$SearchResumeApiModelToJson(this);
}