/*
 * Copyright (c) 1-1/24/23, 10:30 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../main.dart';
import '../models/fellow_worker_response_model.dart';
import '../security/oauth2.dart';
import 'account_utils.dart';

const fellowWorkerHost = "http://127.0.0.1:4334";
const resumeCurrentUserAPI = "/api/v1/employee/current-user-resume";

class ResumeService {
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
      return jsonDecode(utf8.decode(response.bodyBytes))['message'];
    }
  }
}
