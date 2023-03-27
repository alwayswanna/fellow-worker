/*
 * Copyright (c) 2-3/26/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

class ValidationUtils {

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

  static bool validateValue(List<String> values) {
    return values.any((element) => element.isEmpty);
  }
}
