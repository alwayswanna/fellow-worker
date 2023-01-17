/*
 * Copyright (c) 1/17/23, 11:26 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/account_request_model.dart';
import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:http/http.dart' as http;

class AccountService {
  final String clientManagerHost = "http://127.0.0.1:8090";
  final String resumeCreate = "/api/v1/account/create";

  Future<ApiResponseModel> createAccount() async {
    var bodyMessage = jsonEncode(const AccountRequestModel(
        username: "username",
        password: "password",
        email: "email@ya.ru",
        firstName: "firstName",
        middleName: "middleName",
        lastName: "lastName",
        accountType: "COMPANY",
        birthDate: "1985-02-03"));

    print(bodyMessage);
    final response = await http.post(
        Uri.parse(clientManagerHost + resumeCreate),
        headers: {"Content-Type": "application/json"},
        body: bodyMessage);
    print("status: ${response.statusCode}");
    if (response.statusCode == 200) {
      return ApiResponseModel.fromJson(jsonDecode(response.body));
    } else {
      return ApiResponseModel.fromJson(jsonDecode(response.body));
    }
  }
}
