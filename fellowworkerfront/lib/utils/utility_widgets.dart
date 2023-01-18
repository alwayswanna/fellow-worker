/*
 * Copyright (c) 1-1/18/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */


import 'package:flutter/material.dart';

class UtilityWidgets{

  static Future<void> dialogBuilder(BuildContext context, Future<String> response) {
    return showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Создание аккаунта.'),
          content: FutureBuilder<String>(
            future: response,
            builder: (context, snapshot){
              if (snapshot.hasData) {
                return Text(snapshot.data!);
              }else{
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
                Navigator.pushNamed(context, "/");
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
}