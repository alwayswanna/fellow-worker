/*
 * Copyright (c) 2-2/2/23, 11:04 PM
 * Created by https://github.com/alwayswanna
 */

class ValidationService {

  /// Validate selected date before send request
  static bool isValidBirthDate(DateTime selectedDate) {
    var timeNow = DateTime.now();
    var userYearDate = selectedDate.year;
    var userMonthDate = selectedDate.month;
    var userDayDate = selectedDate.day;

    if (userYearDate == timeNow.year &&
        userMonthDate == timeNow.month &&
        userDayDate == timeNow.day) {
      return false;
    } else {
      return true;
    }
  }
}
