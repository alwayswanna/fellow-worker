/*
 * Copyright (c) 1-1/19/23, 11:07 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter_web_auth/flutter_web_auth.dart';

const identifier = 'message';
const secret = 'c29tZXNlY3Rlcg==';

class Oauth2Service {

  void login() async{
    final result = await FlutterWebAuth.authenticate(url: "http://127.0.0.1:9001/oauth2/authorize", callbackUrlScheme: "my-custom-app");

// Extract token from resulting url
    print(Uri.parse(result));
    final token = Uri.parse(result).queryParameters['token'];

  }
  // final authorizationEndpoint =
  //     Uri.parse('http://127.0.0.1:9001/oauth2/authorize');
  // final tokenEndpoint = Uri.parse('http://127.0.0.1:9001/oauth2/token');
  // final redirectUrl = Uri.parse('http://127.0.0.1:8888/oauth2-redirect');
  //
  // final credentialsFile = File('/Users/g.abakshin/Documents');
  //
  // Future<oauth2.Client> createClient() async {
  //   var exists = await credentialsFile.exists();
  //
  //   if (exists) {
  //     var credentials =
  //         oauth2.Credentials.fromJson(await credentialsFile.readAsString());
  //     return oauth2.Client(credentials, identifier: identifier, secret: secret);
  //   }
  //   var grant = oauth2.AuthorizationCodeGrant(
  //       identifier, authorizationEndpoint, tokenEndpoint,
  //       secret: secret);
  //   var authorizationUrl = grant.getAuthorizationUrl(redirectUrl);
  //
  //   await redirect(authorizationUrl);
  //   var responseUrl = await listen(redirectUrl);
  //   return await grant.handleAuthorizationResponse(responseUrl.queryParameters);
  // }
  //
  // redirect(Uri authorizationUrl) async {
  //   if (await canLaunchUrl(Uri.parse(authorizationUrl.toString()))) {
  //   await launchUrl(Uri.parse(authorizationUrl.toString()));
  //   }
  // }
  //
  // listen(Uri redirectUrl) async {
  //   print(redirectUrl.toString());
  // }
  //
  // void makeRequest() async {
  //   var client = await createClient();
  //   await credentialsFile.writeAsString(client.credentials.toJson());
  // }
}
