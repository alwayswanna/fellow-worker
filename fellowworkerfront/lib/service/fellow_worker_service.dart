/*
 * Copyright (c) 1-2/19/23, 11:28 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/vacancy_request_model.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../models/account_response_model.dart';
import '../models/fellow_worker_response_model.dart';
import '../models/resume_request_model.dart';
import 'account_utils.dart';

const fellowWorkerHost = "http://127.0.0.1:4334";
// for employee mappings;
const resumeCurrentUserAPI = "/api/v1/employee/current-user-resume";
const createResumeUserAPI = "/api/v1/employee/create-resume";
const deleteResumeUserAPI = "/api/v1/employee/delete-resume";
const editResumeUserAPI = "/api/v1/employee/edit-resume";
// for company mappings;
const createVacancyUserAPI = "/api/v1/vacancy/create";
const vacanciesCurrentUserAPI = "/api/v1/vacancy/current-user-vacancies";

class FellowWorkerService {

  /// get current user resume, GET
  Future<FellowWorkerResponseModel> currentUserResume(
      FlutterSecureStorage secureStorage
  ) async {
    await RequestUtils.injectJwtToHeaders(secureStorage);
    final requestUri = Uri.parse(fellowWorkerHost + resumeCurrentUserAPI);

    var response = await http.get(requestUri, headers: defaultHeaders);
    RequestUtils.clearRequestHeadersContext();
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

  /// get current user vacancies, GET
  Future<FellowWorkerResponseModel> currentUserVacancies(
      FlutterSecureStorage secureStorage
  ) async {
    await RequestUtils.injectJwtToHeaders(secureStorage);
    final requestUri = Uri.parse(fellowWorkerHost + vacanciesCurrentUserAPI);

    var response = await http.get(requestUri, headers: defaultHeaders);
    RequestUtils.clearRequestHeadersContext();
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

  /// get current user vacancy or resume, GET
  Future<FellowWorkerResponseModel> getCurrenUserEntities(
      FlutterSecureStorage secureStorage,
      Future<ApiResponseModel> response
  ) async {
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

  /// create resume method, POST
  Future<String> createResume(
      FlutterSecureStorage secureStorage,
      ResumeApiModel requestBodyMessage
  ) async {
    var bodyMessage = jsonEncode(requestBodyMessage);

    await RequestUtils.injectJwtToHeaders(secureStorage);

    final requestUri = Uri.parse(fellowWorkerHost + createResumeUserAPI);

    var response =
        await http.post(requestUri, headers: defaultHeaders, body: bodyMessage);
    RequestUtils.clearRequestHeadersContext();

    if (response.statusCode == 200) {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    } else if (response.statusCode == 400) {
      return Future<String>.value(
          "Не все поля заполнены или заполнены в неправильном формате");
    } else {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }

  /// edit resume method, PUT
  Future<String> editResume(FlutterSecureStorage secureStorage,
      ResumeApiModel requestBodyMessage) async {
    var bodyMessage = jsonEncode(requestBodyMessage);

    await RequestUtils.injectJwtToHeaders(secureStorage);

    final requestUri = Uri.parse(fellowWorkerHost + editResumeUserAPI);
    var response =
        await http.put(requestUri, headers: defaultHeaders, body: bodyMessage);

    RequestUtils.clearRequestHeadersContext();

    if (response.statusCode == 200) {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    } else if (response.statusCode == 400) {
      return Future<String>.value(
          "Не все поля заполнены или заполнены в неправильном формате");
    } else {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }

  /// delete resume by id, DELETE
  Future<String> deleteResume(
      FlutterSecureStorage secureStorage,
      String resumeId
  ) async {
    await RequestUtils.injectJwtToHeaders(secureStorage);

    final requestUri =
        Uri.parse("$fellowWorkerHost$deleteResumeUserAPI?id=$resumeId");
    var response = await http.delete(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    return jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// create vacancy, POST
  Future<String> createVacancy(
      FlutterSecureStorage flutterSecureStorage,
      VacancyApiModel vacancyApiModel
  ) async {
    await RequestUtils.injectJwtToHeaders(flutterSecureStorage);

    var bodyMessage = jsonEncode(vacancyApiModel);

    final requestUri = Uri.parse(fellowWorkerHost + createVacancyUserAPI);

    var response = await http.post(
        requestUri,
        headers: defaultHeaders,
        body: bodyMessage
    );
    RequestUtils.clearRequestHeadersContext();

    if (response.statusCode == 200) {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    } else if (response.statusCode == 400) {
      return Future<String>.value(
          "Не все поля заполнены или заполнены в неправильном формате");
    } else {
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }
}
