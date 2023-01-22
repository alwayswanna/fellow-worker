/*
 * Copyright (c) 1-1/22/23, 8:37 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:fellowworkerfront/service/account_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class _ProfileWidget extends State<Profile> {
  late FlutterSecureStorage securityStorage;

  _ProfileWidget({required FlutterSecureStorage sS}) {
    securityStorage = sS;
  }

  late Future<ApiResponseModel> responseModel;

  @override
  void initState() {
    super.initState();
    responseModel = AccountService().getCurrentAccountData(securityStorage);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(builder: (context, snapshot) {
      if (snapshot.hasData) {
        var apiResponse = snapshot.data as ApiResponseModel;
        return Text(apiResponse.message);
      } else {
        return const CircularProgressIndicator();
      }
    });
  }
}

class Profile extends StatefulWidget {
  late FlutterSecureStorage securityStorage;

  Profile({required FlutterSecureStorage sS, super.key}) {
    securityStorage = sS;
  }

  @override
  State<StatefulWidget> createState() {
    return _ProfileWidget(sS: securityStorage);
  }
}
