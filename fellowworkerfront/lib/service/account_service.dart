/*
 * Copyright (c) 1-1/18/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/account_request_model.dart';
import 'package:http/http.dart' as http;

class AccountService {
  final String clientManagerHost = "http://127.0.0.1:8090";
  final String resumeCreate = "/api/v1/account/create";
  final defaultHeaders = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*"
  };

  Future<String> createAccount(
      String username,
      String password,
      String email,
      String firstName,
      String middleName,
      String lastName,
      String accountType,
      String birthDate) async {
    var bodyMessage = jsonEncode(AccountRequestModel(
        username: username,
        password: password,
        email: email,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        accountType: accountType,
        birthDate: birthDate));

    final response = await http.post(
        Uri.parse(clientManagerHost + resumeCreate),
        headers: defaultHeaders,
        body: bodyMessage);
    if (response.statusCode == 200) {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    } else {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }
}
