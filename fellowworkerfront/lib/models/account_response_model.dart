/*
 * Copyright (c) 1-2/8/23, 11:05 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/account_response_model.g.dart';

@JsonSerializable()
class ApiResponseModel {
  final String message;
  final AccountDataModel? accountDataModel;
  final List<AccountDataModel?>? accounts;

  const ApiResponseModel(
      {required this.message,
      required this.accountDataModel,
      required this.accounts});

  factory ApiResponseModel.fromJson(Map<String, dynamic> json) =>
      _$ApiResponseModelFromJson(json);
}

@JsonSerializable(explicitToJson: true)
class AccountDataModel {
  final String username;
  final String firstName;
  final String middleName;
  final String? lastName;
  final String role;
  final String email;
  final String birthDate;

  const AccountDataModel(
      {required this.username,
      required this.email,
      required this.firstName,
      required this.middleName,
      required this.lastName,
      required this.role,
      required this.birthDate});

  factory AccountDataModel.fromJson(Map<String, dynamic> json) =>
      _$AccountDataModelFromJson(json);
}
