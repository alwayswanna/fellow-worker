/*
 * Copyright (c) 1-1/17/23, 12:42 AM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

class FullScreenWidget extends StatelessWidget {
  const FullScreenWidget({super.key});

  final employeeText = "Здесь размещены самые свежие и "
      "проверенные вакансии. \n Чтобы разместить свое резюме перейдите в свой "
      "профиль.";
  final recruiterText = "Здесь размещены самые свежие и "
      "проверенные резюме. \n Чтобы разместить свою вакансию перейдите в свой "
      "профиль.";

  /// Build main page with two cards for employee & recruiter.
  @override
  Widget build(BuildContext context) {
    return Center(
      child: SingleChildScrollView(
        child: ResponsiveGridRow(children: [
          ResponsiveGridCol(
              child: Center(
                  child: SizedBox(
            width: 500,
            child: Padding(
                padding: const EdgeInsets.all(10),
                child: Column(
                  children: [
                    TextField(
                      decoration: InputDecoration(
                        filled: true,
                        fillColor: Colors.white,
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(20)),
                        hintText: 'Найти работу мечты',
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(0, 5, 0, 15),
                      child: ElevatedButton(
                          onPressed: () {
                            Navigator.pushNamed(context, "/");
                          },
                          child: const Padding(
                              padding: EdgeInsets.all(10),
                              child: Icon(Icons.search, shadows: [
                                    Shadow(
                                      color: Colors.purpleAccent,
                                      blurRadius: 10
                                    )
                              ],))),
                    )
                  ],
                )),
          ))),
          ResponsiveGridCol(
              md: 6,
              child: Padding(
                padding: const EdgeInsets.all(30),
                child: buildCard(context, "Соискателям", "assets/working.png",
                    "/employee", employeeText),
              )),
          ResponsiveGridCol(
              md: 6,
              child: Padding(
                  padding: const EdgeInsets.all(30),
                  child: buildCard(context, "Работодателям",
                      "assets/recruitment.png", "/recruiter", recruiterText)))
        ]),
      ),
    );
  }

  /// Build main page for web app.
  static Card buildCard(BuildContext context, String message, String assetPath,
      String routePath, String description) {
    return Card(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20.0),
          side: const BorderSide(
            width: 2.0,
            color: Colors.black,
          ),
        ),
        borderOnForeground: true,
        margin: const EdgeInsets.all(60),
        child: ResponsiveGridRow(children: [
          ResponsiveGridCol(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Padding(
                      padding: const EdgeInsets.fromLTRB(0, 40, 0, 40),
                      child: Image.asset(
                        assetPath,
                        width: 100,
                        height: 100,
                      )),
                  Center(
                    child: Text(message, style: const TextStyle(fontSize: 30.0)),
                  ),
                  Padding(
                    padding: const EdgeInsets.fromLTRB(10, 15, 10, 0),
                    child: Text(description,
                        style: const TextStyle(fontSize: 15, color: Colors.grey)),
                  ),
                  Padding(
                    padding: const EdgeInsets.fromLTRB(0, 100, 0, 20),
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.pushNamed(context, "/");
                      },
                      child: const Padding(
                          padding: EdgeInsets.all(10),
                          child: Text(
                            "Перейти",
                            style: TextStyle(color: Colors.white, fontSize: 30.0),
                          )),
                    ),
                  )
                ],
              ))
        ]),
      );
  }
}
