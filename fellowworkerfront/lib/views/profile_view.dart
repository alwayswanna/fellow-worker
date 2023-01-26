/*
 * Copyright (c) 1-1/26/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/main.dart';
import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:fellowworkerfront/models/fellow_worker_response_model.dart';
import 'package:fellowworkerfront/service/account_utils.dart';
import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../styles/gradient_color.dart';

const deleteActionAccount = "Вы действительно хотите удалить аккаунт?";

class _ProfileWidget extends State<Profile>
    with SingleTickerProviderStateMixin {
  late FlutterSecureStorage securityStorage;
  late ClientManagerService accountService;
  late FellowWorkerService resumeService;

  _ProfileWidget(
      {required FlutterSecureStorage sS,
      required ClientManagerService aS,
      required FellowWorkerService rS}) {
    securityStorage = sS;
    accountService = aS;
    resumeService = rS;
  }

  late Future<ApiResponseModel> clientManagerResponseModel;
  late Future<FellowWorkerResponseModel> fellowWorkerResponseModel;
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    clientManagerResponseModel =
        accountService.getCurrentAccountData(securityStorage);
    fellowWorkerResponseModel = resumeService.getCurrenUserEntities(
        securityStorage, clientManagerResponseModel);
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text("Fellow worker"),
        ),
        body: FutureBuilder(
            future: clientManagerResponseModel,
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
                          RequestUtils.extractAccountType(account.role),
                          6,
                          Colors.black,
                          25),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              dialogBuilderMessage(context,
                                  "Вы действительно хотите удалить аккаунт?");
                            }, "Удалить", 20),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              Navigator.pushNamed(context, "/");
                            }, "Редактировать", 20),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              Navigator.pushNamed(context, "/change-password");
                            }, "Сменить пароль", 20),
                          )),
                      buildResponsiveGridCard(
                          account.role == employeeResponse
                              ? "Ваши активные резюме: "
                              : "Ваши активные вакансии: ",
                          null,
                          Colors.black,
                          30),
                      ResponsiveGridCol(
                          child: FutureBuilder(
                              future: fellowWorkerResponseModel,
                              builder: (context, snapshot) {
                                List<Widget> children;
                                if (snapshot.hasData) {
                                  children = buildUserEntities(
                                      fellowWorkerResponseModel, account);
                                } else if (snapshot.hasError) {
                                  children = <Widget>[
                                    const Icon(
                                      Icons.error_outline,
                                      color: Colors.red,
                                      size: 60,
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.only(top: 16),
                                      child: Text(account.role ==
                                              employeeResponse
                                          ? "Ошибка при получении активных резюме"
                                          : "Ошибка при получении активных вакансий"),
                                    ),
                                  ];
                                } else {
                                  children = <Widget>[
                                    const SizedBox(
                                      width: 60,
                                      height: 60,
                                      child: CircularProgressIndicator(),
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.only(top: 16),
                                      child: Text(account.role ==
                                              employeeResponse
                                          ? "Получение активных резюме .. "
                                          : "Получение активные вакансии .. "),
                                    ),
                                  ];
                                }
                                return Center(
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: children,
                                  ),
                                );
                              }))
                    ],
                  ),
                )),
          ]),
        ),
      ),
    );
  }

  void removeAccount() {
    accountService.removeAccount(securityStorage);
    securityStorage.delete(key: jwtTokenKey);
    Navigator.pushNamed(context, "/");
  }

  Future<void> dialogBuilderMessage(BuildContext context, String message) {
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
              child: const Text('Да'),
              onPressed: () {
                removeAccount();
              },
            ),
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Нет'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            )
          ],
        );
      },
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

  List<Widget> buildUserEntities(
      Future<FellowWorkerResponseModel> fellowWorkerResponseModel,
      AccountDataModel accountDataModel) {
    var role = accountDataModel.role;
    if (role == employeeResponse) {
      return [buildEmployeeResponseWidgets(fellowWorkerResponseModel)];
    } else if (role == companyResponse) {
      return [buildCompanyResponseWidgets(fellowWorkerResponseModel)];
    } else {
      return [
        const Text("Администратор не может иметь активных вакансий и резюме.")
      ];
    }
  }

  Widget buildEmployeeResponseWidgets(
      Future<FellowWorkerResponseModel> fellowWorkerResponseModel) {
    FellowWorkerResponseModel? responseModel;
    fellowWorkerResponseModel.then((value) => responseModel = value);
    if (responseModel != null || responseModel!.resumeResponse != null) {
      var resume = responseModel!.resumeResponse!;
      return ResponsiveGridRow(children: [
        ResponsiveGridCol(md: 3, child: Text(resume.job)),
        ResponsiveGridCol(md: 3, child: Text(resume.lastUpdate)),
        ResponsiveGridCol(
            md: 3,
            child: ElevatedButton(
              onPressed: () {},
              child: const Text("Подробнее"),
            )),
        ResponsiveGridCol(
            md: 3,
            child: ElevatedButton(
              onPressed: () {},
              child: const Text("Удалить"),
            ))
      ]);
    } else {
      return ResponsiveGridCol(child: Text(responseModel!.message));
    }
  }

  Widget buildCompanyResponseWidgets(
      Future<FellowWorkerResponseModel> fellowWorkerResponseModel) {
    FellowWorkerResponseModel? responseModel;
    fellowWorkerResponseModel.then((value) => responseModel = value);
    if (responseModel != null || responseModel!.vacancies != null) {
      var vacancies = responseModel!.vacancies!;
      List<ResponsiveGridCol> responsiveGridCol = <ResponsiveGridCol>[];
      for (var v in vacancies) {
        responsiveGridCol
            .add(ResponsiveGridCol(md: 3, child: Text(v.vacancyName)));
        responsiveGridCol
            .add(ResponsiveGridCol(md: 3, child: Text(v.lastUpdate)));
        responsiveGridCol.add(ResponsiveGridCol(
            md: 3,
            child: ElevatedButton(
                onPressed: () {}, child: const Text("Подробнее"))));
        responsiveGridCol.add(ResponsiveGridCol(
            md: 3,
            child: ElevatedButton(
                onPressed: () {}, child: const Text("Удалить"))));
      }
      return ResponsiveGridRow(children: responsiveGridCol);
    } else {
      return ResponsiveGridCol(child: Text(responseModel!.message));
    }
  }
}

class Profile extends StatefulWidget {
  late FlutterSecureStorage securityStorage;
  late ClientManagerService accountService;
  late FellowWorkerService resumeService;

  Profile(
      {required FlutterSecureStorage sS,
      required ClientManagerService aS,
      required FellowWorkerService rS,
      super.key}) {
    securityStorage = sS;
    accountService = aS;
    resumeService = rS;
  }

  @override
  State<StatefulWidget> createState() {
    return _ProfileWidget(
        sS: securityStorage, aS: accountService, rS: resumeService);
  }
}
