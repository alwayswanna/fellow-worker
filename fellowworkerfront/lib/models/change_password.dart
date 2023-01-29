/*
 * Copyright (c) 1-2/8/23, 11:05 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:json_annotation/json_annotation.dart';

part '../generated/change_password.g.dart';

@JsonSerializable()
class ChangePasswordModel {
  final String oldPassword;
  final String newPassword;

  const ChangePasswordModel(
      {required this.oldPassword, required this.newPassword});

  Map<String, dynamic> toJson() => _$ChangePasswordModelToJson(this);
}
