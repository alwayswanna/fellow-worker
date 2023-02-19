/*
 * Copyright (c) 2-2/7/23, 11:32 PM
 * Created by https://github.com/alwayswanna
 */
import 'package:file_saver/file_saver.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../main.dart';
import '../security/oauth2.dart';
import 'account_utils.dart';

const cvGeneratorHost = "http://127.0.0.1:7044";
const cvGeneratorDownloadApi = "/api/v1/resume-download";

class CvGeneratorService {
  /// method download resume in pdf format
  Future<String> downloadResume(
      FlutterSecureStorage secureStorage, String resumeId) async {
    String? userToken = await secureStorage.read(key: jwtTokenKey);

    Map<dynamic, dynamic> tokenMap =
        Oauth2Service.convertTokenToMap(userToken!);
    String accessToken = tokenMap["access_token"]!;
    defaultHeaders["Authorization"] = "Bearer $accessToken";

    final requestUri =
        Uri.parse("$cvGeneratorHost$cvGeneratorDownloadApi?resumeId=$resumeId");
    final response = await http.get(requestUri, headers: defaultHeaders);

    if (response.statusCode == 200) {
      await FileSaver.instance
          .saveFile("result_cv.pdf", response.bodyBytes, "pdf");
      return "Ваше резюме успешно сохранено ✔️";
    } else {
      return "Ошибка при попытке загрузить резюме";
    }
  }
}
