/*
 * Copyright (c) 2-3/26/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/vacancy/edit_vacancy.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../models/fellow_worker_response_model.dart';
import '../../service/account_utils.dart';
import '../../service/fellow_worker_service.dart';

class _AboutVacancy extends State<AboutVacancy>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late FellowWorkerService _fellowWorkerService;
  late VacancyResponseApiModel _vacancyResponseApiModel;

  @override
  void initState() {
    _fellowWorkerService = widget.fellowWorkerService;
    _vacancyResponseApiModel = widget.vacancyResponseApiModel;
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
    super.initState();
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return UtilityWidgets.buildTopBar(
        UtilityWidgets.buildGradient(buildLayout(), _animationController),
        context);
  }

  Widget buildLayout() {
    return Padding(
      padding: edgeInsets8,
      child: Center(
        child: SingleChildScrollView(
          child: ResponsiveGridRow(children: [
            ResponsiveGridCol(
                child: Center(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(10, 10, 10, 50),
                child: Text(
                  "Ваша открытая вакансия",
                  style: UtilityWidgets.pageTitleStyle(),
                ),
              ),
            )),
            ResponsiveGridCol(
                child: Card(
              shape: UtilityWidgets.buildCardShapes(),
              elevation: 10,
              margin: const EdgeInsets.all(20),
              child: buildVacancyCardWidget(),
            ))
          ]),
        ),
      ),
    );
  }

  Widget buildVacancyCardWidget() {
    var textPadding = const EdgeInsets.fromLTRB(13, 0, 13, 3);
    var resumeDateUpdate = DateTime.parse(_vacancyResponseApiModel.lastUpdate);
    var viewDateTime =
        "${DateFormat.yMMMd().format(resumeDateUpdate)} ${DateFormat.Hm().format(resumeDateUpdate)}";

    String typeOfWork = "Не заполнен";
    typeOfWorkMap.forEach((key, value) {
      if (value == _vacancyResponseApiModel.typeOfWork) {
        typeOfWork = key.replaceAll("  ", "");
      }
    });

    String workFormat = "Не заполнен";
    placementType.forEach((key, value) {
      if (value == _vacancyResponseApiModel.typeOfWorkPlacement) {
        workFormat = key.replaceAll("  ", "");
      }
    });

    String keySkills = "";
    if (_vacancyResponseApiModel.keySkills.isEmpty) {
      keySkills = "Не заполнен";
    } else {
      for (var element in _vacancyResponseApiModel.keySkills) {
        keySkills = "$keySkills \n - $element";
      }
    }

    String responsibilities = "";
    if (_vacancyResponseApiModel.keySkills.isEmpty) {
      responsibilities = "Не заполнен";
    } else {
      for (var element in _vacancyResponseApiModel.workingResponsibilities) {
        responsibilities = "$responsibilities \n - $element";
      }
    }

    var contactApiModel =
        ContactApiModel.fromJson(_vacancyResponseApiModel.contactApiModel);

    String conditions = "";
    if (_vacancyResponseApiModel.companyBonuses.isEmpty) {
      conditions = "Не заполнен";
    } else {
      for (var element in _vacancyResponseApiModel.companyBonuses) {
        conditions = "$conditions \n - $element";
      }
    }

    return Center(
        child: ResponsiveGridRow(
      children: [
        UtilityWidgets.buildResponsiveGridCard(
            _vacancyResponseApiModel.vacancyName, 10, Colors.black, 25),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Заработная плата:", 12, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            _vacancyResponseApiModel.salary, 12, Colors.black, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Наименование компании:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Город:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            _vacancyResponseApiModel.companyName,
            6,
            Colors.black,
            15,
            textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            _vacancyResponseApiModel.cityName,
            6,
            Colors.black,
            15,
            textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Юридический адрес компании:",
            12,
            Colors.blueGrey,
            10,
            textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            _vacancyResponseApiModel.companyFullAddress,
            12,
            Colors.black,
            15,
            textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Тип занятости:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Формат работы:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            typeOfWork, 6, Colors.black, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            workFormat, 6, Colors.black, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Требуемые ключевые навыки:", 12, Colors.blueGrey, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            keySkills, 6, Colors.black, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Чем вам предстоит заниматься:",
            12,
            Colors.blueGrey,
            15,
            textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            responsibilities, 6, Colors.black, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Условия:", 12, Colors.blueGrey, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            conditions, 6, Colors.black, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Контактные данные:", 12, Colors.blueGrey, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "ФИО:", 12, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            contactApiModel.fio, 12, Colors.black, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Номер телефона:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Адрес электронной почты:", 6, Colors.blueGrey, 10, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            contactApiModel.phone, 6, Colors.black, 15, textPadding),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            contactApiModel.email, 6, Colors.black, 15, textPadding),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCardWithPadding(
            "Дата последнего обновления: $viewDateTime",
            12,
            Colors.black,
            15,
            textPadding),
        UtilityWidgets.emptyLine(),
        ResponsiveGridCol(
            md: 2,
            child: Padding(
                padding: const EdgeInsets.all(20),
                child: UtilityWidgets.buildCardButtonPadding(() {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => EditVacancy(
                              fWS: _fellowWorkerService,
                              vRAM: _vacancyResponseApiModel)));
                }, "Редактировать", 18.0, 13.0))),
        ResponsiveGridCol(
            md: 2,
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: UtilityWidgets.buildCardButtonPadding(() {
                UtilityWidgets.dialogBuilderApi(
                    context,
                    _fellowWorkerService
                        .deleteVacancy(_vacancyResponseApiModel.resumeId),
                    "Удалить резюме",
                    "/profile");
              }, "Удалить", 18.0, 13.0),
            ))
      ],
    ));
  }
}

class AboutVacancy extends StatefulWidget {
  late final FellowWorkerService fellowWorkerService;
  late final VacancyResponseApiModel vacancyResponseApiModel;

  AboutVacancy({
    required FellowWorkerService fWS,
    required VacancyResponseApiModel vacancy,
    super.key
  }) {
    fellowWorkerService = fWS;
    vacancyResponseApiModel = vacancy;
  }

  @override
  createState() => _AboutVacancy();
}
