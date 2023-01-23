/*
 * Copyright (c) 1-1/23/23, 11:18 PM
 * Created by https://github.com/alwayswanna
 */

class ApiResponseModel {
  final String message;
  final AccountDataModel? accountDataModel;
  final List<AccountDataModel?>? accounts;

  const ApiResponseModel(
      {required this.message,
      required this.accountDataModel,
      required this.accounts});

  factory ApiResponseModel.fromJson(Map<String, dynamic> json) {
    List<AccountDataModel> accounts = List.empty();
    if (json['accounts'] != null) {
      accounts = (json['accounts'] as List)
          .map((e) => AccountDataModel.fromJson(e))
          .toList();
    }

    return ApiResponseModel(
        message: json['message'],
        accountDataModel: AccountDataModel.fromJson(json['accountDataModel']),
        accounts: accounts);
  }
}

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

  factory AccountDataModel.fromJson(Map<String, dynamic> json) {
    return AccountDataModel(
        username: json['username'],
        email: json['email'],
        firstName: json['firstName'],
        middleName: json['middleName'],
        lastName: json['lastName'],
        role: json['role'],
        birthDate: json['birthDate']);
  }
}
