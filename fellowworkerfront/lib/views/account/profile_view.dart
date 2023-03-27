/*
 * Copyright (c) 1-3/26/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/account_response_model.dart';
import 'package:fellowworkerfront/models/fellow_worker_response_model.dart';
import 'package:fellowworkerfront/service/account_utils.dart';
import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/resume/about_resume.dart';
import 'package:fellowworkerfront/views/resume/create_resume_view.dart';
import 'package:fellowworkerfront/views/vacancy/about_vacancy.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../service/cv_generator_service.dart';

const deleteActionAccount = "Вы действительно хотите удалить аккаунт?";
const paddingButtons = EdgeInsets.fromLTRB(0, 0, 5, 0);

class _ProfileWidget extends State<Profile>
    with SingleTickerProviderStateMixin {
  late ClientManagerService _accountService;
  late FellowWorkerService _fellowWorkerService;
  late CvGeneratorService _cvGeneratorService;
  late Future<ApiResponseModel> _clientManagerResponseModel;
  late Future<FellowWorkerResponseModel> _fellowWorkerResponseModel;
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _accountService = widget.accountService;
    _fellowWorkerService = widget.resumeService;
    _cvGeneratorService = widget.cvGeneratorService;
    _clientManagerResponseModel =
        _accountService.getCurrentAccountData();
    _fellowWorkerResponseModel = _fellowWorkerService.getCurrenUserEntities(
        _clientManagerResponseModel
    );
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
                return UtilityWidgets.buildGradient(
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
      padding: edgeInsets8,
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
                            padding: edgeInsets10,
                            child: UtilityWidgets.buildCardButton(() {
                              dialogBuilderMessage(context,
                                  "Вы действительно хотите удалить аккаунт?");
                            }, "Удалить", 15),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: edgeInsets10,
                            child: UtilityWidgets.buildCardButton(() {
                              Navigator.pushNamed(context, "/edit-account");
                            }, "Редактировать", 15),
                          )),
                      ResponsiveGridCol(
                          md: 4,
                          child: Padding(
                            padding: edgeInsets10,
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
                                    UtilityWidgets.buildResponsiveGridCartText(account.role ==
                                        employeeResponse
                                        ? "Ошибка при получении активных резюме"
                                        : "Ошибка при получении активных вакансий", Colors.black, 15)
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
    _accountService.removeAccount();
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
                            fS: _fellowWorkerService
                        )
                    )
                );
              }, "Подробнее", 15)),
          ResponsiveGridCol(
            md: 3,
            child: IconButton(
              iconSize: 20,
              icon: const Icon(Icons.download),
              onPressed: () {
                UtilityWidgets.dialogBuilderApi(
                    context,
                    _cvGeneratorService.downloadResume(resume.resumeId),
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
                padding: edgeInsets10,
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
    if (fellowWorkerResponseModel.vacancies != null &&
        fellowWorkerResponseModel.vacancies!.isNotEmpty) {
      var vacancies = fellowWorkerResponseModel.vacancies!;
      List<ResponsiveGridCol> responsiveGridCol = <ResponsiveGridCol>[];
      responsiveGridCol.addAll([
        UtilityWidgets.buildResponsiveGridCard(
            "Наименование:", 3, Colors.grey, 15),
        UtilityWidgets.buildResponsiveGridCard(
            "Дата изменения:", 3, Colors.grey, 15),
        UtilityWidgets.buildResponsiveGridCard("", 3, Colors.grey, 10),
        UtilityWidgets.buildResponsiveGridCard("", 3, Colors.grey, 10),
      ]);

      for (var v in vacancies) {
        var vacancyUpdateTime = DateTime.parse(v.lastUpdate);
        var viewDateTime =
            "${DateFormat.yMMMd().format(vacancyUpdateTime)} ${DateFormat.Hm().format(vacancyUpdateTime)}";
        responsiveGridCol.add(UtilityWidgets.buildResponsiveGridCard(
            v.vacancyName, 3, Colors.black, 15));
        responsiveGridCol.add(
          UtilityWidgets.buildResponsiveGridCard(
              viewDateTime, 3, Colors.black, 15),
        );
        responsiveGridCol.add(ResponsiveGridCol(
            md: 3,
            child: UtilityWidgets.buildCardButton(() {
              Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => AboutVacancy(
                          fWS: _fellowWorkerService,
                          vacancy: v
                      )));
            }, "Подробнее", 15)));
        responsiveGridCol.add(ResponsiveGridCol(
            md: 3,
            child: IconButton(onPressed: (){
              UtilityWidgets.dialogBuilderApi(
                  context,
                  _fellowWorkerService.deleteVacancy(v.resumeId),
                  "Удаление вакансии",
                  "/profile");
            },
            icon: const Icon(Icons.delete_forever_outlined))
        ));
      }
      responsiveGridCol.add(ResponsiveGridCol(
          md: 4,
          child: Padding(
            padding: edgeInsets10,
            child: UtilityWidgets.buildCardButtonPadding(() {
              Navigator.pushNamed(context, "/create-vacancy");
            }, "Добавить новую вакансию", 15, 3),
          )));
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
                  padding: edgeInsets10,
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
  late final ClientManagerService accountService;
  late final FellowWorkerService resumeService;
  late final CvGeneratorService cvGeneratorService;

  Profile({
    required ClientManagerService aS,
    required FellowWorkerService rS,
    required CvGeneratorService cG,
    super.key
  }) {
    accountService = aS;
    resumeService = rS;
    cvGeneratorService = cG;
  }

  @override
  createState() => _ProfileWidget();
}
