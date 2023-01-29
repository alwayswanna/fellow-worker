/*
 * Copyright (c) 1-1/28/23, 6:20 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter/material.dart';

import '../service/account_utils.dart';

class UtilityWidgets {
  static String extractAccountRoleDataFromWidget(String? accountRoleWidget) {
    if (accountRoleWidget == null || accountRoleWidget.isEmpty) {
      return "";
    }

    if (accountRoleWidget == accountTypes.first) {
      return "EMPLOYEE";
    } else if (accountRoleWidget == accountTypes.last) {
      return "COMPANY";
    } else {
      return "EMPLOYEE";
    }
  }

  static Future<void> dialogBuilderApi(BuildContext context,
      Future<String> response, String message, String onOkRedirectionPath) {
    return showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(message),
          content: FutureBuilder<String>(
            future: response,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                return Text(snapshot.data!);
              } else {
                return const CircularProgressIndicator();
              }
            },
          ),
          actions: <Widget>[
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Ok'),
              onPressed: () {
                Navigator.pushNamed(context, onOkRedirectionPath);
              },
            ),
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Закрыть'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  static Future<void> dialogBuilderMessage(
      BuildContext context, String message) {
    return showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Оповещение'),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Закрыть'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  static Padding buildTextField(TextEditingController controller, String hint) {
    return Padding(
        padding: const EdgeInsets.all(10),
        child: TextField(
          controller: controller,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(20)),
            hintText: hint,
          ),
        ));
  }

  static TextStyle pageTitleStyle() {
    return const TextStyle(
      color: Colors.white,
      fontSize: 35,
      fontWeight: FontWeight.bold,
      shadows: <Shadow>[
        Shadow(
          offset: Offset(10.0, 10.0),
          blurRadius: 20.0,
          color: Color.fromARGB(255, 0, 0, 0),
        )
      ],
    );
  }

  static TextStyle cardTextStyle(Color color, double fontSize) {
    return TextStyle(
      color: color,
      fontSize: fontSize,
      fontWeight: FontWeight.bold,
    );
  }

  static Widget buildCardButton(
      VoidCallback voidCallback, String message, double fontSize) {
    return ElevatedButton(
      onPressed: () {
        voidCallback();
      },
      child: Padding(
          padding: const EdgeInsets.all(10),
          child: Text(
            message,
            style: TextStyle(color: Colors.white, fontSize: fontSize),
          )),
    );
  }

  static RoundedRectangleBorder buildCardShapes() {
    return RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(20.0),
      side: const BorderSide(
        width: 2.0,
        color: Colors.black,
      ),
    );
  }
}
