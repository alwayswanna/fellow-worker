/*
 * Copyright (c) 1-1/28/23, 2:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../service/account_utils.dart';
import '../styles/gradient_color.dart';

const padding = EdgeInsets.all(10);

class _Registration extends State<Registration>
    with SingleTickerProviderStateMixin {
  final ClientManagerService accountService = ClientManagerService();
  late AnimationController _animationController;
  late String? typeAccount;
  late bool _passwordVisibility;
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
        firstDate: DateTime(1900, 8),
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
    _passwordVisibility = false;
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
                          child: Text("Создать аккаунт",
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
                  child: Container(
                      padding: padding,
                      child: TextField(
                        controller: passwordController,
                        enableSuggestions: false,
                        obscureText: !_passwordVisibility,
                        autocorrect: false,
                        decoration: InputDecoration(
                            suffixIcon: IconButton(
                              icon: Icon(
                                _passwordVisibility
                                    ? Icons.visibility
                                    : Icons.visibility_off,
                                color: Theme.of(context).primaryColorDark,
                              ),
                              onPressed: () {
                                setState(() {
                                  _passwordVisibility = !_passwordVisibility;
                                });
                              },
                            ),
                            hintText: "Пароль:",
                            filled: true,
                            fillColor: Colors.white,
                            border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(20))),
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

  Future<void> sendRequestCreateAccount() async {
    var type = UtilityWidgets.extractAccountRoleDataFromWidget(typeAccount);

    var response = accountService.createAccount(
        usernameController.text,
        passwordController.text,
        emailController.text,
        controllerFirstName.text,
        controllerMiddleName.text,
        controllerLastName.text,
        type,
        selectedDate.toIso8601String());
    UtilityWidgets.dialogBuilderApi(context, response, "Создание аккаунта", '/');
  }
}

class Registration extends StatefulWidget {
  const Registration({super.key});

  @override
  State<StatefulWidget> createState() {
    return _Registration();
  }
}
