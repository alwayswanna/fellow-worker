/*
 * Copyright (c) 1-1/29/23, 12:12 AM
 * Created by https://github.com/alwayswanna
 */

const employeeResponse = "EMPLOYEE";
const companyResponse = "COMPANY";
const employeeType = 'Соискатель';
const companyType = 'Работодатель';
const adminType = 'Администратор';

const accountTypes = ["  Соискатель", "  Работодатель"];
const educationLevels = ["  Специалитет", "  Бакалавриат"];

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
}
