/*
 * Copyright (c) 1-1/21/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter_web_auth/src/flutter_web_auth_web.dart';
import 'package:flutter_web_plugins/flutter_web_plugins.dart';

// ignore: public_member_api_docs
void registerPlugins(Registrar registrar) {
  FlutterWebAuthPlugin.registerWith(registrar);
  registrar.registerMessageHandler();
}