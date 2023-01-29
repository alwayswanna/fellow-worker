/*
 * Copyright (c) 1-2/19/23, 11:28 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/main.dart';
import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:fellowworkerfront/models/fellow_worker_response_model.dart';
import 'package:fellowworkerfront/service/account_utils.dart';
import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/resume/about_resume.dart';
import 'package:fellowworkerfront/views/resume/create_resume_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:intl/intl.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../service/cv_generator_service.dart';
import '../../styles/gradient_color.dart';

const deleteActionAccount = "Вы действительно хотите удалить аккаунт?";

class _ProfileWidget extends State<Profile> with SingleTickerProviderStateMixin {

  late FlutterSecureStorage _securityStorage;
  late ClientManagerService _accountService;
  late FellowWorkerService _fellowWorkerService;
  late CvGeneratorService _cvGeneratorService;
  late Future<ApiResponseModel> _clientManagerResponseModel;
  late Future<FellowWorkerResponseModel> _fellowWorkerResponseModel;
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _securityStorage = widget.securityStorage;
    _accountService = widget.accountService;
    _fellowWorkerService = widget.resumeService;
    _cvGeneratorService = widget.cvGeneratorService;
    _clientManagerResponseModel =
        _accountService.getCurrentAccountData(_securityStorage);
    _fellowWorkerResponseModel = _fellowWorkerService.getCurrenUserEntities(
        _securityStorage, _clientManagerResponseModel);
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
    return UtilityWidgets.buildTopBar(
        FutureBuilder(
            future: _clientManagerResponseModel,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var apiResponse = snapshot.data as ApiResponseModel;
                return GradientEnchanted.buildGradient(
                    buildPageLayout(apiResponse), _animationController);
              } else {
                return UtilityWidgets.sizedProgressiveBar(100, 100);
              }
            }),
        context);
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
                      UtilityWidgets.buildResponsiveGridCard(
                          responseModel.message, null, Colors.black, 30),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Username:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.username, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Email:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.email, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Имя:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.firstName, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Фамилия:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.middleName, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Отчество:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.lastName, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Дата рождения:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.birthDate, 6, Colors.black, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          "Тип аккаунта:", 6, Colors.blueGrey, 15),
                      UtilityWidgets.buildResponsiveGridCard(
                          RequestUtils.extractAccountType(account.role),
                          6,
                          Colors.black,
                          15),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              dialogBuilderMessage(context,
                                  "Вы действительно хотите удалить аккаунт?");
                            }, "Удалить", 15),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              Navigator.pushNamed(context, "/edit-account");
                            }, "Редактировать", 15),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: UtilityWidgets.buildCardButton(() {
                              Navigator.pushNamed(context, "/change-password");
                            }, "Сменить пароль", 15),
                          )),
                      UtilityWidgets.buildResponsiveGridCard(
                          account.role == employeeResponse
                              ? "Ваши активные резюме: "
                              : "Ваши активные вакансии: ",
                          null,
                          Colors.black,
                          30),
                      ResponsiveGridCol(
                          child: FutureBuilder(
                              future: _fellowWorkerResponseModel,
                              builder: (context, snapshot) {
                                Widget children;
                                if (snapshot.hasData) {
                                  var fellowWorkerResponse = snapshot.data
                                      as FellowWorkerResponseModel;
                                  children = buildUserEntities(
                                      fellowWorkerResponse, account);
                                } else if (snapshot.hasError) {
                                  children = ResponsiveGridRow(children: [
                                    ResponsiveGridCol(
                                        child: const Icon(
                                      Icons.error_outline,
                                      color: Colors.red,
                                      size: 60,
                                    )),
                                    ResponsiveGridCol(
                                        child: Padding(
                                      padding: const EdgeInsets.only(top: 16),
                                      child: Text(account.role ==
                                              employeeResponse
                                          ? "Ошибка при получении активных резюме"
                                          : "Ошибка при получении активных вакансий"),
                                    ))
                                  ]);
                                } else {
                                  children = ResponsiveGridRow(children: [
                                    ResponsiveGridCol(
                                      child: Center(
                                        child: Padding(
                                            padding: padding,
                                            child: UtilityWidgets
                                                .sizedProgressiveBar(100, 100)),
                                      ),
                                    ),
                                  ]);
                                }
                                return Center(child: children);
                              }))
                    ],
                  ),
                )),
          ]),
        ),
      ),
    );
  }

  ResponsiveGridRow buildErrorFieldRow(AccountDataModel account) {
    var widgetText = account.role == employeeResponse
        ? "Ошибка при получении активных резюме"
        : "Ошибка при получении активных вакансий";
    return ResponsiveGridRow(children: [
      ResponsiveGridCol(
          child: const Icon(
        Icons.error_outline,
        color: Colors.red,
        size: 60,
      )),
      ResponsiveGridCol(
          child: Padding(
        padding: const EdgeInsets.only(top: 16),
        child: UtilityWidgets.buildResponsiveGridCard(
            widgetText, 6, Colors.black, 15),
      ))
    ]);
  }

  void removeAccount() {
    _accountService.removeAccount(_securityStorage);
    _securityStorage.delete(key: jwtTokenKey);
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

  Widget buildUserEntities(FellowWorkerResponseModel fellowWorkerResponseModel,
      AccountDataModel accountDataModel) {
    var role = accountDataModel.role;
    if (role == employeeResponse) {
      return buildEmployeeResponseWidgets(fellowWorkerResponseModel);
    } else if (role == companyResponse) {
      return buildCompanyResponseWidgets(fellowWorkerResponseModel);
    } else {
      return ResponsiveGridRow(children: [
        UtilityWidgets.buildResponsiveGridCard(
            "Администратор не может иметь активных вакансий и резюме.",
            12,
            Colors.black,
            15)
      ]);
    }
  }

  Padding buildEmployeeResponseWidgets(
      FellowWorkerResponseModel fellowWorkerResponseModel) {
    if (fellowWorkerResponseModel.resumeResponse != null) {
      var resume = fellowWorkerResponseModel.resumeResponse!;
      var resumeDateUpdate = DateTime.parse(resume.lastUpdate);
      var viewDateTime =
          "${DateFormat.yMMMd().format(resumeDateUpdate)} ${DateFormat.Hm().format(resumeDateUpdate)}";

      return Padding(
        padding: const EdgeInsets.fromLTRB(0, 0, 0, 15),
        child: ResponsiveGridRow(children: [
          UtilityWidgets.buildResponsiveGridCard(
              "Наименование:", 3, Colors.grey, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Дата изменения:", 3, Colors.grey, 15),
          UtilityWidgets.buildResponsiveGridCard("", 3, Colors.grey, 10),
          UtilityWidgets.buildResponsiveGridCard("", 3, Colors.grey, 10),
          UtilityWidgets.buildResponsiveGridCard(
              resume.job, 3, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              viewDateTime, 3, Colors.black, 15),
          ResponsiveGridCol(
              md: 3,
              child: UtilityWidgets.buildCardButton(() {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => AboutResume(
                            resume: resume,
                            fS: _fellowWorkerService,
                            fSS: _securityStorage)));
              }, "Подробнее", 15)),
          ResponsiveGridCol(
            md: 3,
            child: IconButton(
              iconSize: 20,
              icon: const Icon(Icons.download),
              onPressed: () {
                UtilityWidgets.dialogBuilderApi(
                    context,
                    _cvGeneratorService.downloadResume(
                        _securityStorage, resume.resumeId),
                    "Загрузка резюме",
                    "/profile");
              },
            ),
          )
        ]),
      );
    } else {
      return Padding(
        padding: const EdgeInsets.fromLTRB(0, 0, 0, 15),
        child: ResponsiveGridRow(children: [
          UtilityWidgets.buildResponsiveGridCard(
              fellowWorkerResponseModel.message, 4, Colors.black, 15),
          ResponsiveGridCol(md: 4, child: const Center()),
          ResponsiveGridCol(
              md: 4,
              child: Padding(
                padding: const EdgeInsets.all(10.0),
                child: UtilityWidgets.buildCardButtonPadding(() {
                  Navigator.pushNamed(context, "/create-resume");
                }, "Создать резюме", 15, 3),
              ))
        ]),
      );
    }
  }

  ResponsiveGridRow buildCompanyResponseWidgets(
      FellowWorkerResponseModel fellowWorkerResponseModel) {
    if (fellowWorkerResponseModel.vacancies!.isNotEmpty) {
      var vacancies = fellowWorkerResponseModel.vacancies!;
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
      return ResponsiveGridRow(children: [
        ResponsiveGridCol(
            child: Padding(
          padding: const EdgeInsets.fromLTRB(0, 0, 0, 15),
          child: ResponsiveGridRow(children: [
            UtilityWidgets.buildResponsiveGridCard(
                fellowWorkerResponseModel.message, 4, Colors.black, 15),
            ResponsiveGridCol(md: 4, child: const Center()),
            ResponsiveGridCol(
                md: 4,
                child: Padding(
                  padding: const EdgeInsets.all(10.0),
                  child: UtilityWidgets.buildCardButtonPadding(() {
                    Navigator.pushNamed(context, "/create-vacancy");
                  }, "Создать вакансию", 15, 3),
                ))
          ]),
        ))
      ]);
    }
  }
}

class Profile extends StatefulWidget {
  late final FlutterSecureStorage securityStorage;
  late final ClientManagerService accountService;
  late final FellowWorkerService resumeService;
  late final CvGeneratorService cvGeneratorService;

  Profile(
      {required FlutterSecureStorage sS,
      required ClientManagerService aS,
      required FellowWorkerService rS,
      required CvGeneratorService cG,
      super.key}) {
    securityStorage = sS;
    accountService = aS;
    resumeService = rS;
    cvGeneratorService = cG;
  }

  @override
  createState() => _ProfileWidget();
}
