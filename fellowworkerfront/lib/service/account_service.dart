/*
 * Copyright (c) 1-1/22/23, 8:37 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/account_request_model.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../main.dart';
import '../models/account_response_model.dart';
import '../security/oauth2.dart';

const String clientManagerHost = "http://127.0.0.1:8090";
const String resumeCreate = "/api/v1/account/create";
const String currentResume = "/api/v1/account/current";
final defaultHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*"
};

class AccountService {
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

  /// Method get info by current account with Oauth2.
  Future<ApiResponseModel> getCurrentAccountData(
      FlutterSecureStorage secureStorage) async {
    String? userToken = await secureStorage.read(key: jwtTokenKey);

    Map<dynamic, dynamic> tokenMap =
        Oauth2Service.convertTokenToMap(userToken!);
    String accessToken = tokenMap["access_token"]!;
    defaultHeaders["Authorization"] = "Bearer $accessToken";
    final requestUri = Uri.parse(clientManagerHost + currentResume);
    final response = await http.get(requestUri, headers: defaultHeaders);

    if (response.statusCode == 200) {
      print("response: ${utf8.decode(response.bodyBytes)}");
      return ApiResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      print("response: ${utf8.decode(response.bodyBytes)}");
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }
}
