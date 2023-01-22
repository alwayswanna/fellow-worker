/*
 * Copyright (c) 1-1/22/23, 11:57 PM
 * Created by https://github.com/alwayswanna
 */

const employeeResponse = "EMPOYEE";
const employeeType = 'Соискатель';
const companyType = 'Company';

class AccountUtils {

  static String extractAccountType(String responseEnum) {
    if (responseEnum == employeeResponse) {
      return employeeType;
    } else {
      return companyType;
    }
  }
}
