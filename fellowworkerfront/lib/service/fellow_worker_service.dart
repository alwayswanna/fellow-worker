/*
 * Copyright (c) 1-3/9/23, 11:54 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:fellowworkerfront/models/search_resume_model.dart';
import 'package:fellowworkerfront/models/search_vacancy_model.dart';
import 'package:fellowworkerfront/models/vacancy_request_model.dart';
import 'package:fellowworkerfront/security/oauth2.dart';
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
const vacancyDeleteUserAPI = "/api/v1/vacancy/delete-id";
const vacancyEditUserAPI = "/api/v1/vacancy/edit";

// mappings for all
const searchVacanciesAPI = "/api/v1/vacancy/vacancy-all";
const filterVacanciesAPI = "/api/v1/vacancy/filter-vacancies";
const searchResumesAPI = "/api/v1/employee/get-all-resume";
const filterResumeAPI = "/api/v1/employee/filter-resumes";

class FellowWorkerService {

  final Oauth2Service oauth2service;

  FellowWorkerService(this.oauth2service);

  /// get current user resume, GET
  Future<FellowWorkerResponseModel> currentUserResume() async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

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
  Future<FellowWorkerResponseModel> currentUserVacancies() async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

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
      Future<ApiResponseModel> response
  ) async {
    var accountModel = await response;

    if (accountModel.accountDataModel?.role == companyResponse) {
      return await currentUserVacancies();
    } else if (accountModel.accountDataModel?.role == employeeResponse) {
      return currentUserResume();
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
      ResumeApiModel requestBodyMessage
  ) async {
    var bodyMessage = jsonEncode(requestBodyMessage);

    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse(fellowWorkerHost + createResumeUserAPI);

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

  /// edit resume method, PUT
  Future<String> editResume(
      
      ResumeApiModel requestBodyMessage
  ) async {
    var bodyMessage = jsonEncode(requestBodyMessage);

    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

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
      String resumeId
  ) async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri =
        Uri.parse("$fellowWorkerHost$deleteResumeUserAPI?id=$resumeId");
    var response = await http.delete(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    return jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// create vacancy, POST
  Future<String> createVacancy(
      VacancyApiModel vacancyApiModel
  ) async {
    var bodyMessage = jsonEncode(vacancyApiModel);

    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

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

  /// delete vacancy, DELETE
  Future<String> deleteVacancy(String vacancyId) async {
    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri =
    Uri.parse("$fellowWorkerHost$vacancyDeleteUserAPI?vacancyId=$vacancyId");

    var response = await http.delete(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    return jsonDecode(utf8.decode(response.bodyBytes))['message'];
  }

  /// edit vacancy, PUT
  Future<String> editVacancy(VacancyApiModel vacancyApiModel) async {
    var bodyMessage = jsonEncode(vacancyApiModel);

    var userToken = await oauth2service.getAccessToken();
    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse(fellowWorkerHost + vacancyEditUserAPI);

    var response = await http.put(
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

  /// get all vacancies, GET
  Future<FellowWorkerResponseModel> searchAllVacancies() async {
    final uriRequest = Uri.parse(fellowWorkerHost + searchVacanciesAPI);

    var response = await http.get(uriRequest,headers: defaultHeaders);

    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null
      );
    }
  }

  /// filter vacancy, POST
  Future<FellowWorkerResponseModel> filterVacancy(
      SearchVacancyApiModel searchVacancyApiModel
  ) async{
    var bodyMessage = jsonEncode(searchVacancyApiModel);
    final uriRequest = Uri.parse(fellowWorkerHost + filterVacanciesAPI);

    var response = await http.post(
        uriRequest,
        headers: defaultHeaders,
        body: bodyMessage
    );

    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes))
      );
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null
      );
    }
  }

  /// get all resume, GET
  Future<FellowWorkerResponseModel> searchAllResume() async {
    final uriRequest = Uri.parse(fellowWorkerHost + searchResumesAPI);

    var response = await http.get(uriRequest, headers: defaultHeaders);

    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null
      );
    }
  }

  /// filter resumes, POST
  Future<FellowWorkerResponseModel> filterResume(
      SearchResumeApiModel searchVacancyApiModel
  ) async{
    var bodyMessage = jsonEncode(searchVacancyApiModel);
    final uriRequest = Uri.parse(fellowWorkerHost + filterResumeAPI);

    var response = await http.post(
        uriRequest,
        headers: defaultHeaders,
        body: bodyMessage
    );

    if (response.statusCode == 200) {
      return FellowWorkerResponseModel.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes))
      );
    } else {
      return FellowWorkerResponseModel(
          message: jsonDecode(utf8.decode(response.bodyBytes))['message'],
          resumeResponse: null,
          resumes: null,
          vacancyResponse: null,
          vacancies: null
      );
    }
  }
}
