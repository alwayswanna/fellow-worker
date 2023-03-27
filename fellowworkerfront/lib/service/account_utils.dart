/*
 * Copyright (c) 1-3/27/23, 8:01 PM
 * Created by https://github.com/alwayswanna
 */

import 'dart:convert';

import 'package:file_picker/file_picker.dart';
import 'package:uuid/uuid.dart';

const employeeResponse = "EMPLOYEE";
const companyResponse = "COMPANY";

const typeOfAccount = {
  'EMPLOYEE': 'Соискатель',
  'COMPANY': 'Работодатель',
  'ADMIN': 'Администратор'
};

const typeOfWorkMap = {
  '  Полная занятость': 'FULL_EMPLOYMENT',
  '  Частичная занятость': 'PART_TIME_EMPLOYMENT'
};

const placementType = {'  Офис': 'OFFICE', '  Удаленная': 'REMOTE'};

const accountTypesMap = {
  "  Соискатель": "EMPLOYEE",
  "  Работодатель": "COMPANY"
};

const educationLevelMap = {
  "  Специалитет": "SPECIALTY",
  "  Бакалавриат": "BACHELOR",
  "  Магистрант": "MAGISTRACY"
};

const uuid = Uuid();

final defaultHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*"
};

class RequestUtils {

  static String extractAccountType(String responseEnum) {
    return typeOfAccount[responseEnum]!;
  }

  static void clearRequestHeadersContext() {
    defaultHeaders.remove("Authorization");
  }

  static void injectTokenToRequest(String userToken) {
    defaultHeaders["Authorization"] = "Bearer $userToken";
  }

  static String? buildBase64Image(PlatformFile? file) {
    return file == null ? null : base64Encode(file.bytes!);
  }

  static String? buildExtension(PlatformFile? file) {
    return file?.extension;
  }
}
