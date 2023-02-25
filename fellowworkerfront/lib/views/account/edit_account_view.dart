/*
 * Copyright (c) 1-3/9/23, 8:15 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../service/account_utils.dart';
import '../../service/validation_service.dart';
import '../../styles/gradient_color.dart';
import '../../utils/utility_widgets.dart';

const padding = EdgeInsets.all(10);
const String editAccountUserMessage = "Изменение аккаунта";
const String backSuccessMessageCheck =
    "Данные вашего аккаунт успешно обновлены.";

class _EditCurrentAccount extends State<EditCurrentAccount>
    with SingleTickerProviderStateMixin {
  late ClientManagerService clientManagerService;
  late AnimationController _animationController;

  late String? typeAccount;
  var controllerFirstName = TextEditingController();
  var controllerMiddleName = TextEditingController();
  var controllerLastName = TextEditingController();
  var emailController = TextEditingController();
  var dateController = TextEditingController();
  var usernameController = TextEditingController();
  DateTime selectedDate = DateTime.now();

  @override
  void initState() {
    super.initState();
    clientManagerService = widget.clientManagerService;
    typeAccount = null;
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
  }

  @override
  Widget build(BuildContext context) {
    return UtilityWidgets.buildTopBar(
        GradientEnchanted.buildGradient(buildLayout(), _animationController),
        context
    );
  }

  Widget buildLayout() {
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
                                      style:
                                          const TextStyle(color: Colors.black),
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
                              sendRequestToEditAccount();
                            },
                            style: ElevatedButton.styleFrom(
                                shape: const RoundedRectangleBorder(
                                    borderRadius:
                                        BorderRadius.all(Radius.circular(5)))),
                            child: const Padding(
                                padding: padding,
                                child: Text(
                                  "Изменить аккаунт",
                                  style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold),
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
        helpText: "Выбранная дата",
        fieldLabelText: "Выберете дату: ",
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

  Future<void> sendRequestToEditAccount() async {
    var type = UtilityWidgets.extractAccountRoleDataFromWidget(typeAccount);

    String? requestDate = ValidationService.isValidBirthDate(selectedDate)
        ? selectedDate.toIso8601String()
        : null;

    if (usernameController.text.isEmpty &&
        emailController.text.isEmpty &&
        controllerFirstName.text.isEmpty &&
        controllerMiddleName.text.isEmpty &&
        controllerLastName.text.isEmpty &&
        type.isEmpty &&
        requestDate == null) {
      UtilityWidgets.dialogBuilderApi(
          context,
          Future.value("Вы должны заполнить хотя бы одно поле"),
          editAccountUserMessage,
          '/profile');
    } else {
      var messageResponse = await clientManagerService.editAccount(
          usernameController.text,
          emailController.text,
          controllerFirstName.text,
          controllerMiddleName.text,
          controllerLastName.text,
          type,
          requestDate,
      );

      var message = messageResponse == backSuccessMessageCheck
          ? backSuccessMessageCheck
          : "Введите только те данные которые вы бы хотели изменить\n$messageResponse";

      UtilityWidgets.dialogBuilderApi(
          context, Future.value(message), editAccountUserMessage, '/profile');
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}

class EditCurrentAccount extends StatefulWidget {
  late final ClientManagerService clientManagerService;

  EditCurrentAccount({
    required ClientManagerService cM,
    super.key
  }) {
    clientManagerService = cM;
  }

  @override
  createState() => _EditCurrentAccount();
}
