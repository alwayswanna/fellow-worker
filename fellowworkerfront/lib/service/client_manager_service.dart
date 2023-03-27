/*
 * Copyright (c) 1-3/27/23, 8:01 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/account_request_model.dart';
import 'package:fellowworkerfront/models/change_password.dart';
import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:http/http.dart' as http;

import '../models/account_response_model.dart';
import 'account_utils.dart';

const String clientManagerHost = "http://127.0.0.1:8090";
const String accountCreateAPI = "/api/v1/account/create";
const String accountEditAPI = "/api/v1/account/edit";
const String currentAccountAPI = "/api/v1/account/current";
const String deleteAccountAPI = "/api/v1/account/delete";
const String changePasswordAPI = "/api/v1/account/change-password";

class ClientManagerService {
  final Oauth2Service oauth2service;

  ClientManagerService(this.oauth2service);

  /// Method which send request to create new account.
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
        Uri.parse(clientManagerHost + accountCreateAPI),
        headers: defaultHeaders,
        body: bodyMessage);
    return response.statusCode == 200
        ? jsonDecode(utf8.decode(response.bodyBytes))['message']
        : jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// Method which send request to edit current account data.
  Future<String> editAccount(
    String? username,
    String? email,
    String? firstName,
    String? middleName,
    String? lastName,
    String? accountType,
    String? birthDate,
  ) async {
    var role = accountType!.isEmpty ? null : accountType;

    var bodyMessage = jsonEncode(AccountRequestModel(
        username: username,
        password: null,
        email: email,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        accountType: role,
        birthDate: birthDate));

    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse(clientManagerHost + accountEditAPI);

    final response = await http.put(
        requestUri,
        headers: defaultHeaders,
        body: bodyMessage
    );

    RequestUtils.clearRequestHeadersContext();
    return response.statusCode == 200
        ? jsonDecode(utf8.decode(response.bodyBytes))['message']
        : jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// Method get info by current account with Oauth2.
  Future<ApiResponseModel> getCurrentAccountData() async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse(clientManagerHost + currentAccountAPI);
    final response = await http.get(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    return response.statusCode == 200
        ? ApiResponseModel.fromJson(jsonDecode(utf8.decode(response.bodyBytes)))
        : jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// Method remove account by authorized request.
  Future<String> removeAccount() async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse(clientManagerHost + deleteAccountAPI);
    final response = await http.delete(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    /* clear session */
    await oauth2service.clearStorage();

    return jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// Method which send request to change password for current user.
  Future<String> changePassword(String oldPassword, String newPassword) async {
    var bodyMessage = jsonEncode(ChangePasswordModel(
        oldPassword: oldPassword, newPassword: newPassword));
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final response = await http.put(
        Uri.parse(clientManagerHost + changePasswordAPI),
        headers: defaultHeaders,
        body: bodyMessage);

    RequestUtils.clearRequestHeadersContext();

    return jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }
}
