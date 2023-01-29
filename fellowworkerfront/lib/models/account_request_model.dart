/*
 * Copyright (c) 1-2/8/23, 11:05 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/account_request_model.g.dart';

@JsonSerializable()
class AccountRequestModel {
  final String? username;
  final String? password;
  final String? email;
  final String? firstName;
  final String? middleName;
  final String? lastName;
  final String? accountType;
  final String? birthDate;

  const AccountRequestModel(
      {required this.username,
      required this.password,
      required this.email,
      required this.firstName,
      required this.middleName,
      required this.lastName,
      required this.accountType,
      required this.birthDate});

  factory AccountRequestModel.fromJson(Map<String, dynamic> json) => _$AccountRequestModelFromJson(json);

  Map<String, dynamic> toJson() => _$AccountRequestModelToJson(this);
}
