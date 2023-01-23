/*
 * Copyright (c) 1-1/23/23, 11:18 PM
 * Created by https://github.com/alwayswanna
 */

class ChangePasswordModel {
  final String oldPassword;
  final String newPassword;

  const ChangePasswordModel(
      {required this.oldPassword, required this.newPassword});

  Map<String, dynamic> toJson() {
    return {'oldPassword': oldPassword, 'newPassword': newPassword};
  }
}
