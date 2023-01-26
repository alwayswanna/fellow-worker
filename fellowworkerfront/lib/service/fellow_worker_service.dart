/*
 * Copyright (c) 1-1/26/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../main.dart';
import '../models/account_response_model.dart';
import '../models/fellow_worker_response_model.dart';
import '../security/oauth2.dart';
import 'account_utils.dart';

const fellowWorkerHost = "http://127.0.0.1:4334";
const resumeCurrentUserAPI = "/api/v1/employee/current-user-resume";
const vacanciesCurrentUserAPI = "/api/v1/vacancy/current-user-vacancies";

class FellowWorkerService {

  /// Get current user resume.
  Future<FellowWorkerResponseModel> currentUserResume(
      FlutterSecureStorage secureStorage) async {
    String? userToken = await secureStorage.read(key: jwtTokenKey);

    Map<dynamic, dynamic> tokenMap =
    Oauth2Service.convertTokenToMap(userToken!);
    String accessToken = tokenMap["access_token"]!;
    defaultHeaders["Authorization"] = "Bearer $accessToken";
    final requestUri = Uri.parse(fellowWorkerHost + resumeCurrentUserAPI);

    var response = await http.get(requestUri, headers: defaultHeaders);
    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null);
    }
  }

  /// Get current user vacancies.
  Future<FellowWorkerResponseModel> currentUserVacancies(
      FlutterSecureStorage secureStorage) async {
    String? userToken = await secureStorage.read(key: jwtTokenKey);

    Map<dynamic, dynamic> tokenMap =
    Oauth2Service.convertTokenToMap(userToken!);
    String accessToken = tokenMap["access_token"]!;
    defaultHeaders["Authorization"] = "Bearer $accessToken";
    final requestUri = Uri.parse(fellowWorkerHost + vacanciesCurrentUserAPI);

    var response = await http.get(requestUri, headers: defaultHeaders);
    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null);
    }
  }

  Future<FellowWorkerResponseModel> getCurrenUserEntities(
      FlutterSecureStorage secureStorage,
      Future<ApiResponseModel> response) async {
    var accountModel = await response;

    if (accountModel.accountDataModel?.role == companyResponse) {
      return await currentUserVacancies(secureStorage);
    } else if (accountModel.accountDataModel?.role == employeeResponse) {
      return currentUserResume(secureStorage);
    } else {
      return Future.value(const FellowWorkerResponseModel(
          message: "Произошла ошибка",
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null));
    }
  }
}
