/*
 * Copyright (c) 1-1/27/23, 10:22 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../service/account_utils.dart';
import '../styles/gradient_color.dart';
import '../utils/utility_widgets.dart';

class _EditCurrentAccount extends State<EditCurrentAccount>
    with SingleTickerProviderStateMixin {
  late FlutterSecureStorage securityStorage;
  late FellowWorkerService fellowWorkerService;
  late AnimationController _animationController;

  late String? typeAccount;
  var controllerFirstName = TextEditingController();
  var controllerMiddleName = TextEditingController();
  var controllerLastName = TextEditingController();
  var emailController = TextEditingController();
  var dateController = TextEditingController();
  var usernameController = TextEditingController();
  DateTime selectedDate = DateTime.now();

  _EditCurrentAccount(
      {required FlutterSecureStorage sS, required FellowWorkerService fS}) {
    securityStorage = sS;
    fellowWorkerService = fS;
  }

  @override
  void initState() {
    super.initState();
    typeAccount = accountTypes.first;
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Fellow worker"),
      ),
      body:
          GradientEnchanted.buildGradient(buildLayout(), _animationController),
    );
  }

  Widget buildLayout() {
    var padding = const EdgeInsets.all(10);
    return Center(
      child: SingleChildScrollView(
        child: SizedBox(
          width: 800,
            child: SizedBox(
                width: 400,
                child: ResponsiveGridRow(
                  children: [
                    ResponsiveGridCol(
                        child: Center(
                            child: Padding(
                                padding: padding,
                                child: Text("Редактировать аккаунт",
                                    style: UtilityWidgets.pageTitleStyle())))),
                    ResponsiveGridCol(
                        child: Container(
                            width: 400,
                            padding: padding,
                            child: TextField(
                              controller: usernameController,
                              decoration: InputDecoration(
                                filled: true,
                                fillColor: Colors.white,
                                border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(20)),
                                hintText: "Имя пользователя:",
                              ),
                            ))),
                    ResponsiveGridCol(
                        md: 6,
                        child: UtilityWidgets.buildTextField(
                            controllerFirstName, "Имя:")),
                    ResponsiveGridCol(
                        md: 6,
                        child: UtilityWidgets.buildTextField(
                            controllerMiddleName, "Фамилия:")),
                    ResponsiveGridCol(
                        md: 6,
                        child: UtilityWidgets.buildTextField(
                            controllerLastName, "Отчество:")),
                    ResponsiveGridCol(
                        md: 6,
                        child: UtilityWidgets.buildTextField(
                            emailController, "Адрес эл. почты:")),
                    ResponsiveGridCol(
                        md: 6,
                        child: Container(
                            margin: const EdgeInsets.all(10.0),
                            decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.circular(20.0),
                                border: Border.all(color: Colors.black)),
                            child: DropdownButtonHideUnderline(
                              child: DropdownButton<String>(
                                  hint: const Text("  Тип аккаунта:"),
                                  value: typeAccount,
                                  onChanged: (value) {
                                    setState(() {
                                      typeAccount = value!;
                                    });
                                  },
                                  items: accountTypes.map((String text) {
                                    return DropdownMenuItem<String>(
                                      value: text,
                                      child: Text(
                                        text,
                                        style: const TextStyle(color: Colors.black),
                                      ),
                                    );
                                  }).toList()),
                            ))),
                    ResponsiveGridCol(
                        md: 6,
                        child: Padding(
                          padding: const EdgeInsets.all(10),
                          child: TextField(
                              controller: dateController,
                              //editing controller of this TextField
                              decoration: InputDecoration(
                                filled: true,
                                fillColor: Colors.white,
                                border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(20)),
                                hintText:
                                " Дата рождения: ${selectedDate.year}/${selectedDate.month}/${selectedDate.day}",
                              ),
                              readOnly: true,
                              // when true user cannot edit text
                              onTap: () async {
                                _selectDate(context);
                              }),
                        )),
                    ResponsiveGridCol(
                      md: 6,
                      child: Container(),
                    ),
                    ResponsiveGridCol(
                        md: 6,
                        child: Padding(
                          padding: padding,
                          child: ElevatedButton(
                              onPressed: () {
                                print("send data");
                              },
                              style: ElevatedButton.styleFrom(
                                  shape: const RoundedRectangleBorder(
                                      borderRadius:
                                      BorderRadius.all(Radius.circular(5)))),
                              child: Padding(
                                  padding: padding,
                                  child: const Text(
                                    "Изменить аккаунт",
                                    style: TextStyle(
                                        fontSize: 20, fontWeight: FontWeight.bold),
                                  ))),
                        ))
                  ],
                )),
        ),
      ),
    );
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
        context: context,
        initialDate: selectedDate,
        firstDate: DateTime(1900, 8),
        lastDate: DateTime(2101));
    if (picked != null && picked != selectedDate) {
      setState(() {
        selectedDate = picked;
      });
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}

class EditCurrentAccount extends StatefulWidget {
  late final FlutterSecureStorage securityStorage;
  late final FellowWorkerService fellowWorkerService;

  EditCurrentAccount(
      {required FlutterSecureStorage sS,
      required FellowWorkerService fS,
      super.key}) {
    securityStorage = sS;
    fellowWorkerService = fS;
  }

  @override
  State<StatefulWidget> createState() {
    return _EditCurrentAccount(sS: securityStorage, fS: fellowWorkerService);
  }
}
