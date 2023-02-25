/*
 * Copyright (c) 1-3/13/23, 10:54 PM
 * Created by https://github.com/alwayswanna
 */
import 'dart:convert';
import 'dart:developer';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';
import 'package:http/http.dart' as http;
import 'package:jwt_decoder/jwt_decoder.dart';

import '../main.dart';

const identifier = 'message';
const secret = 'c29tZXNlY3Rlcg==';
const oath2ServerAddress = '127.0.0.1:9001';
String basicToken = stringToBase64.encode("$identifier:$secret");

final oauth2RequestURI = Uri.http(oath2ServerAddress, 'oauth2/token');
Codec<String, String> stringToBase64 = utf8.fuse(base64);

class Oauth2Service {
  final FlutterSecureStorage secureStorage;

  Oauth2Service(this.secureStorage);

  /// Method returns access token
  Future<String> getAccessToken() async {
    String? storedToken = await secureStorage.read(key: accessToken);

    if (storedToken == null) {
      return login();
    } else {
      if(!JwtDecoder.isExpired(storedToken)){
        return storedToken;
      }

      var expiredRefreshTokenDate = JwtDecoder.getExpirationDate(storedToken)
          .add(const Duration(days: refreshTokenTimeToLive));
      bool isExpiredRefresh = expiredRefreshTokenDate.isBefore(DateTime.now());

      if (!isExpiredRefresh) {
        return refresh();
      } else {
        return login();
      }
    }
  }

  /// Method start login action
  Future<String> login() async {
    log("Start login action");

    final currentUri = Uri.base;
    final redirectUri = Uri(
      host: currentUri.host,
      scheme: currentUri.scheme,
      port: currentUri.port,
      path: '/auth.html',
    );

    final url = Uri.http(oath2ServerAddress, 'oauth2/authorize', {
      'response_type': 'code',
      'client_id': identifier,
      'redirect_uri': redirectUri.toString(),
      'scope': 'openid',
    });

    final result = await FlutterWebAuth.authenticate(
        url: url.toString(),
        callbackUrlScheme:
        currentUri.scheme
    );

    final code = Uri.parse(result).queryParameters['code'];

    final response = await http.post(oauth2RequestURI, body: {
      'redirect_uri': redirectUri.toString(),
      'grant_type': 'authorization_code',
      'client_secret': secret,
      'client_id': identifier,
      'code': code,
    });

    await convertAndSaveResponseBodyToUserAuthSession(response.body);

    var accessTokeValue = await secureStorage.read(key: accessToken);
    return accessTokeValue!;
  }

  /// Method refresh user token
  Future<String> refresh() async {
    log("Start refresh user session");

    var headers = {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Basic $basicToken',
      'Cache-Control': 'no-cache'
    };

    log("request headers: $headers");
    var refreshTokenValue = await secureStorage.read(key: refreshToken);

    var request = http.Request('POST', oauth2RequestURI);

    request.bodyFields = {
      'grant_type': 'refresh_token',
      'refresh_token': '$refreshTokenValue'
    };
    request.headers.addAll(headers);

    http.StreamedResponse response = await request.send();

    if (response.statusCode == 200) {
      var responseBody = await response.stream.bytesToString();
      await convertAndSaveResponseBodyToUserAuthSession(responseBody);

      var accessTokeValue = await secureStorage.read(key: accessToken);
      return accessTokeValue!;
    } else {
      return login();
    }
  }

  /// Method clean-up user cookies
  Future<void> clearStorage() async {
    secureStorage.delete(key: accessToken);
    secureStorage.delete(key: refreshToken);
  }

  /// Method validate user session
  Future<bool> userContainSession() async {
    return await secureStorage.containsKey(key: accessToken);
  }

  /// Method converts JWT token to Map<String, String>
  Map<dynamic, dynamic> convertTokenToMap(String token) {
    Map jsonMap = jsonDecode(token);
    return jsonMap;
  }

  /// Create or update user session
  Future<void> convertAndSaveResponseBodyToUserAuthSession(String responseBody) async {
    var tokenMap = convertTokenToMap(responseBody);
    String accessTokenValue = tokenMap["access_token"]!;
    String refreshTokenValue = tokenMap["refresh_token"]!;

    /* save user session */
    await securityStorage.write(key: accessToken, value: accessTokenValue);
    await securityStorage.write(key: refreshToken, value: refreshTokenValue);

    log("OAuth2 session credentials successfully saved");
  }
}
