/*
 * Copyright (c) 1-1/17/23, 11:26 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/account_service.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../styles/gradient_color.dart';

class _RegistrationList extends State<Registration>
    with SingleTickerProviderStateMixin {
  final AccountService accountService = AccountService();
  late AnimationController _animationController;
  late String? typeAccount;
  var accountTypes = ["  Соискатель", "  Работодатель"];
  var padding = const EdgeInsets.all(10);
  var controllerFirstName = TextEditingController();
  var controllerMiddleName = TextEditingController();
  var controllerLastName = TextEditingController();
  var emailController = TextEditingController();
  var dateController = TextEditingController();
  var usernameController = TextEditingController();
  var passwordController = TextEditingController();
  DateTime selectedDate = DateTime.now();

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
        context: context,
        initialDate: selectedDate,
        firstDate: DateTime(2015, 8),
        lastDate: DateTime(2101));
    if (picked != null && picked != selectedDate) {
      setState(() {
        selectedDate = picked;
      });
    }
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

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
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
                          child: const Text("Создать аккаунт",
                              style: TextStyle(
                                color: Colors.black,
                                fontSize: 35,
                                fontWeight: FontWeight.bold,
                                shadows: <Shadow>[
                                  Shadow(
                                    offset: Offset(10.0, 10.0),
                                    blurRadius: 20.0,
                                    color: Color.fromARGB(255, 0, 0, 0),
                                  )
                                ],
                              ))))),
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
                  child: Container(
                      padding: padding,
                      child: TextField(
                        obscureText: true,
                        controller: passwordController,
                        decoration: InputDecoration(
                          filled: true,
                          fillColor: Colors.white,
                          border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(20)),
                          hintText: "Пароль:",
                        ),
                      ))),
              ResponsiveGridCol(
                  md: 6, child: buildTextField(controllerFirstName, "Имя:")),
              ResponsiveGridCol(
                  md: 6,
                  child: buildTextField(controllerMiddleName, "Фамилия:")),
              ResponsiveGridCol(
                  md: 6,
                  child: buildTextField(controllerLastName, "Отчество:")),
              ResponsiveGridCol(
                  md: 6,
                  child: buildTextField(emailController, "Адрес эл. почты:")),
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
                          sendRequestCreateAccount();
                        },
                        style: ElevatedButton.styleFrom(
                            shape: const RoundedRectangleBorder(
                                borderRadius:
                                    BorderRadius.all(Radius.circular(5)))),
                        child: Padding(
                            padding: padding,
                            child: const Text(
                              "Создать аккаунт",
                              style: TextStyle(
                                  fontSize: 20, fontWeight: FontWeight.bold),
                            ))),
                  ))
            ],
          )),
    )));
  }

  String sendRequestCreateAccount() {
    var response = accountService.createAccount();
    print(response);
    return "";
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

class Registration extends StatefulWidget {
  const Registration({super.key});

  @override
  State<StatefulWidget> createState() {
    return _RegistrationList();
  }
}