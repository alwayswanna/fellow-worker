/*
 * Copyright (c) 2-3/9/23, 11:54 PM
 * Created by https://github.com/alwayswanna
 */
import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:file_saver/file_saver.dart';
import 'package:http/http.dart' as http;

import 'account_utils.dart';

const cvGeneratorHost = "http://127.0.0.1:7044";
const cvGeneratorDownloadApi = "/api/v1/resume-download";

class CvGeneratorService {

  final Oauth2Service oauth2service;

  CvGeneratorService(this.oauth2service);

  /// method download resume in pdf format
  Future<String> downloadResume(String resumeId) async {
    var userToken = await oauth2service.getAccessToken();

    RequestUtils.injectTokenToRequest(userToken);

    final requestUri = Uri.parse("$cvGeneratorHost$cvGeneratorDownloadApi?resumeId=$resumeId");
    final response = await http.get(requestUri, headers: defaultHeaders);

    RequestUtils.clearRequestHeadersContext();

    if (response.statusCode == 200) {
      await FileSaver.instance
          .saveFile("result_cv.pdf", response.bodyBytes, "pdf");
      return "Ваше резюме успешно сохранено ✔️";
    } else {
      return "Ошибка при попытке загрузить резюме";
    }
  }
}
