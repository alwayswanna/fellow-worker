/*
 * Copyright (c) 1-1/28/23, 2:59 PM
 * Created by https://github.com/alwayswanna
 */

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

  factory AccountRequestModel.fromJson(Map<String, dynamic> json) {
    return AccountRequestModel(
        username: json['username'],
        password: json['password'],
        email: json['email'],
        firstName: json['firstName'],
        middleName: json['middleName'],
        lastName: json['lastName'],
        accountType: json['accountType'],
        birthDate: json['birthDate']);
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'password': password,
      'email': email,
      'firstName': firstName,
      'middleName': middleName,
      'lastName': lastName,
      'accountType': accountType,
      'birthDate': birthDate
    };
  }
}
