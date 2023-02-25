/*
 * Copyright (c) 1-2/20/23, 11:22 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:file_picker/file_picker.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:uuid/uuid.dart';

import '../main.dart';
import '../security/oauth2.dart';

const employeeResponse = "EMPLOYEE";
const companyResponse = "COMPANY";
const employeeType = 'Соискатель';
const companyType = 'Работодатель';
const adminType = 'Администратор';
const undergraduate = 'BACHELOR';
const specialist = 'SPECIALTY';
const master = 'MAGISTRACY';

const typeOfWorkMap = {
  '  Полная занятость' : 'FULL_EMPLOYMENT',
  '  Частичная занятость':'PART_TIME_EMPLOYMENT'
};

const placementType = {
  '  Офис':'OFFICE',
  '  Удаленная':'REMOTE'
};

const accountTypes = ["  Соискатель", "  Работодатель"];
const educationLevels = ["  Специалитет", "  Бакалавриат", "  Магистрант"];

const uuid = Uuid();

final defaultHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*"
};

class RequestUtils {
  static String extractAccountType(String responseEnum) {
    if (responseEnum == employeeResponse) {
      return employeeType;
    } else if (responseEnum == companyResponse) {
      return companyType;
    } else {
      return adminType;
    }
  }

  static void clearRequestHeadersContext() {
    defaultHeaders.remove("Authorization");
  }

  static Future<void> injectJwtToHeaders(FlutterSecureStorage flutterSecureStorage) async {
    String? userToken = await flutterSecureStorage.read(key: jwtTokenKey);
    var tokenMap = Oauth2Service.convertTokenToMap(userToken!);
    String accessToken = tokenMap["access_token"]!;
    defaultHeaders["Authorization"] = "Bearer $accessToken";
  }

  static String? buildBase64Image(PlatformFile? file) {
    if (file == null) {
      return null;
    }

    return base64Encode(file.bytes!);
  }

  static String? buildExtension(PlatformFile? file) {
    if (file == null) {
      return null;
    }

    return file.extension;
  }
}
