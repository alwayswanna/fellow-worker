/*
 * Copyright (c) 1-1/22/23, 11:57 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:fellowworkerfront/service/account_service.dart';
import 'package:fellowworkerfront/service/account_utils.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../styles/gradient_color.dart';

const deleteActionAccount = "Вы действительно хотите удалить аккаунт?";

class _ProfileWidget extends State<Profile>
    with SingleTickerProviderStateMixin {
  late FlutterSecureStorage securityStorage;

  _ProfileWidget({required FlutterSecureStorage sS}) {
    securityStorage = sS;
  }

  late Future<ApiResponseModel> responseModel;
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    responseModel = AccountService().getCurrentAccountData(securityStorage);
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
        body: FutureBuilder(
            future: responseModel,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var apiResponse = snapshot.data as ApiResponseModel;
                return GradientEnchanted.buildGradient(
                    buildPageLayout(apiResponse), _animationController);
              } else {
                return const Center(
                  widthFactor: 300,
                  heightFactor: 300,
                  child: CircularProgressIndicator(
                    color: Colors.cyan,
                    semanticsValue: 'Загрузка ...',
                  ),
                );
              }
            }));
  }

  Widget buildPageLayout(ApiResponseModel responseModel) {
    var account = responseModel.accountDataModel!;
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Center(
        child: SingleChildScrollView(
          child: ResponsiveGridRow(children: [
            ResponsiveGridCol(
                child: Center(
              child: Padding(
                  padding: const EdgeInsets.fromLTRB(10, 10, 10, 50),
                  child: Text("Личная информация",
                      style: UtilityWidgets.pageTitleStyle())),
            )),
            ResponsiveGridCol(
                md: 6,
                child: Image.asset(
                  "assets/working.png",
                  width: 400,
                  height: 400,
                )),
            ResponsiveGridCol(
                md: 6,
                child: Card(
                  shape: UtilityWidgets.buildCardShapes(),
                  elevation: 10,
                  margin: const EdgeInsets.all(30),
                  child: ResponsiveGridRow(
                    children: [
                      buildResponsiveGridCard(
                          responseModel.message, null, Colors.black, 30),
                      buildResponsiveGridCard(
                          "Username:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.username, 6, Colors.black, 25),
                      buildResponsiveGridCard("Email:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.email, 6, Colors.black, 25),
                      buildResponsiveGridCard("Имя:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.firstName, 6, Colors.black, 25),
                      buildResponsiveGridCard(
                          "Фамилия:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.middleName, 6, Colors.black, 25),
                      buildResponsiveGridCard(
                          "Отчество:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.lastName, 6, Colors.black, 25),
                      buildResponsiveGridCard(
                          "Дата рождения:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          account.birthDate, 6, Colors.black, 25),
                      buildResponsiveGridCard(
                          "Тип аккаунта:", 6, Colors.blueGrey, 25),
                      buildResponsiveGridCard(
                          AccountUtils.extractAccountType(account.role),
                          6,
                          Colors.black,
                          25),
                      buildCardButton(4, "Удалить", 20, null),
                      buildCardButton(4, "Редактировать", 20, null),
                      buildCardButton(4, "Сменить пароль", 20, null),
                    ],
                  ),
                )),
          ]),
        ),
      ),
    );
  }

  ResponsiveGridCol buildResponsiveGridCard(
      String? message, int? md, Color fontColor, double fontSize) {
    return ResponsiveGridCol(
        md: md,
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Text(message ?? "Не указано",
              style: UtilityWidgets.cardTextStyle(fontColor, fontSize)),
        ));
  }

  ResponsiveGridCol buildCardButton(int? md, String message, double fontSize,
      VoidCallbackAction? voidCallbackAction) {
    return ResponsiveGridCol(
        md: md,
        child: Padding(
          padding: const EdgeInsets.all(10.0),
          child: UtilityWidgets.buildCardButton(() {
            voidCallbackAction;
          }, message, fontSize),
        ));
  }
}

class Profile extends StatefulWidget {
  late FlutterSecureStorage securityStorage;

  Profile({required FlutterSecureStorage sS, super.key}) {
    securityStorage = sS;
  }

  @override
  State<StatefulWidget> createState() {
    return _ProfileWidget(sS: securityStorage);
  }
}
