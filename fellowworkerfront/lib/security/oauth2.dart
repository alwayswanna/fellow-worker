/*
 * Copyright (c) 1-1/21/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';
import 'package:http/http.dart' as http;

import '../main.dart';

const identifier = 'message';
const secret = 'c29tZXNlY3Rlcg==';

class Oauth2Service {

  void login(FlutterSecureStorage securityStorage) async {
    final currentUri = Uri.base;
    final redirectUri = Uri(
      host: currentUri.host,
      scheme: currentUri.scheme,
      port: currentUri.port,
      path: '/auth.html',
    );


    final url = Uri.http('127.0.0.1:9001', 'oauth2/authorize', {
      'response_type': 'code',
      'client_id': identifier,
      'redirect_uri': redirectUri.toString(),
      'scope': 'openid',
    });

    final result = await FlutterWebAuth.authenticate(
        url: url.toString(), callbackUrlScheme: currentUri.scheme);

    final code = Uri.parse(result).queryParameters['code'];

    final urlPost = Uri.http('127.0.0.1:9001', 'oauth2/token');

    final response = await http.post(urlPost, body: {
      'redirect_uri': redirectUri.toString(),
      'grant_type': 'authorization_code',
      'client_secret': secret,
      'client_id': identifier,
      'code': code,
    });

    securityStorage.write(key: jwtTokenKey, value: response.body);
  }
}
