/*
 * Copyright (c) 1-1/23/23, 11:18 PM
 * Created by https://github.com/alwayswanna
 */

const employeeResponse = "EMPOYEE";
const companyResponse = "COMPANY";
const employeeType = 'Соискатель';
const companyType = 'Работодатель';
const adminType = 'Администратор';

class AccountUtils {

  static String extractAccountType(String responseEnum) {
    if (responseEnum == employeeResponse) {
      return employeeType;
    } else if (responseEnum == companyResponse) {
      return companyType;
    }else{
      return adminType;
    }
  }
}
