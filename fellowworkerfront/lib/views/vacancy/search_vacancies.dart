/*
 * Copyright (c) 2-3/22/23, 8:22 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/search_vacancy_model.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/styles/gradient_color.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../models/fellow_worker_response_model.dart';
import '../../service/account_utils.dart';
import '../../utils/value_pickers.dart';
import '../account/edit_account_view.dart';

class _SearchVacancies extends State<SearchVacancies>
    with SingleTickerProviderStateMixin {
  late FellowWorkerService _fellowWorkerService;
  late AnimationController _animationController;
  late Future<FellowWorkerResponseModel> _fellowWorkerResponseModel;

  var cityNameTEC = TextEditingController();
  var keySkillTEC = TextEditingController();
  var typePlacementMap = {uuid.v1().toString(): TextEditingController()};
  var typeTimeMap = {uuid.v1().toString(): TextEditingController()};

  @override
  void initState() {
    _fellowWorkerService = widget.fellowWorkerService;
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
    _fellowWorkerResponseModel = _fellowWorkerService.searchAllVacancies();
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
        GradientEnchanted.buildGradient(buildLayout(), _animationController),
        context);
  }

  Widget buildLayout() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Center(
          child: SingleChildScrollView(
              child: ResponsiveGridRow(children: [
        ResponsiveGridCol(md: 3, child: buildFilter()),
        ResponsiveGridCol(md: 9, child: buildVacanciesView()),
      ]))),
    );
  }

  Widget buildFilter() {
    return Card(
        shape: UtilityWidgets.buildCardShapes(),
        elevation: 10,
        margin: const EdgeInsets.all(20),
        child: Center(
            child: SingleChildScrollView(
          child: ResponsiveGridRow(
            children: [
              UtilityWidgets.buildResponsiveGridCartText(
                  "Фильтры:", Colors.black, 15),
              UtilityWidgets.emptyLine(),
              UtilityWidgets.buildResponsiveGridCartText(
                  "Город:", Colors.blueGrey, 10),
              ResponsiveGridCol(
                  child: UtilityWidgets.buildTextField(cityNameTEC, "Город:")),
              UtilityWidgets.emptyLine(),
              UtilityWidgets.buildResponsiveGridCartText(
                  "Ключевые навыки:", Colors.blueGrey, 10),
              ResponsiveGridCol(
                  child: UtilityWidgets.buildTextField(
                      keySkillTEC, "Ключевые навыки:")),
              UtilityWidgets.emptyLine(),
              UtilityWidgets.buildResponsiveGridCartText(
                  "Формат работы:", Colors.blueGrey, 10),
              buildDropDown("Офис/Удаленная", typePlacementMap, placementType),
              UtilityWidgets.emptyLine(),
              UtilityWidgets.buildResponsiveGridCartText(
                  "Тип занятости:", Colors.blueGrey, 10),
              buildDropDown("Полная/Частичная", typeTimeMap, typeOfWorkMap),
              ResponsiveGridCol(
                  child: Padding(
                      padding: const EdgeInsets.all(8),
                      child: UtilityWidgets.buildCardButton(() {
                        filterAllLoadedVacancies();
                      }, "Искать", 15)))
            ],
          ),
        )));
  }

  ResponsiveGridCol buildDropDown(
      String message,
      Map<String, TextEditingController> mapTec,
      Map<String, String> dropDownValues) {
    return ResponsiveGridCol(
        child: StateDropdownButtonWidget(
            tec: mapTec,
            dV: dropDownValues.keys.toList(),
            id: mapTec.keys.first.toString(),
            m: message));
  }

  Widget buildVacanciesView() {
    return FutureBuilder(
        future: _fellowWorkerResponseModel,
        builder: (context, snapshot) {
          Widget children;
          if (snapshot.hasData) {
            var fellowWorkerResponse =
                snapshot.data as FellowWorkerResponseModel;
            children = buildVacancyEntities(fellowWorkerResponse);
          } else if (snapshot.hasError) {
            children = ResponsiveGridRow(children: [
              ResponsiveGridCol(
                  child: const Icon(
                Icons.error_outline,
                color: Colors.red,
                size: 60,
              )),
              ResponsiveGridCol(
                  child: const Padding(
                padding: EdgeInsets.only(top: 16),
                child: Text("Ошибка при получении активных вакансий"),
              ))
            ]);
          } else {
            children = ResponsiveGridRow(children: [
              ResponsiveGridCol(
                child: Center(
                  child: Padding(
                      padding: padding,
                      child: UtilityWidgets.sizedProgressiveBar(100, 100)),
                ),
              ),
            ]);
          }
          return Center(child: children);
        });
  }

  Widget buildVacancyEntities(
      FellowWorkerResponseModel fellowWorkerResponseModel) {
    var vacancies = fellowWorkerResponseModel.vacancies;
    var columns = <ResponsiveGridCol>[];
    if (vacancies != null && vacancies.isNotEmpty) {
      for (var v in vacancies) {
        var salary = v.salary ?? " ~ ";
        columns.add(ResponsiveGridCol(
            child: Card(
          shape: UtilityWidgets.buildCardShapes(),
          elevation: 10,
          margin: const EdgeInsets.all(20),
          child: ResponsiveGridRow(
            children: [
              UtilityWidgets.buildResponsiveGridCard(
                  v.vacancyName, 12, Colors.black, 20),
              UtilityWidgets.buildResponsiveGridCard(
                  v.companyName, 2, Colors.grey, 13),
              ResponsiveGridCol(md: 2, child: Container()),
              ResponsiveGridCol(md: 2, child: Container()),
              ResponsiveGridCol(md: 2, child: Container()),
              ResponsiveGridCol(md: 2, child: Container()),
              UtilityWidgets.buildResponsiveGridCard(
                  "$salary ₽", 2, Colors.green, 20),
              ResponsiveGridCol(
                  md: 3,
                  child: Padding(
                    padding: const EdgeInsets.all(8),
                    child: UtilityWidgets.buildCardButton(() {
                      buildAboutVacancy(v, context);
                    }, "Подробнее", 13),
                  ))
            ],
          ),
        )));
      }
    }

    return ResponsiveGridRow(children: columns);
  }

  Future<void> buildAboutVacancy(
      VacancyResponseApiModel vacancy, BuildContext context) {
    return showDialog<void>(
        context: context,
        builder: (BuildContext context) {
          return Card(
              shape: UtilityWidgets.buildCardShapes(),
              elevation: 10,
              margin: const EdgeInsets.all(25),
              child: SingleChildScrollView(
                child: ResponsiveGridRow(
                  children: [
                    ResponsiveGridCol(
                        md: 1,
                        child: Padding(
                          padding: const EdgeInsets.all(10),
                          child: IconButton(
                            onPressed: () {
                              Navigator.of(context).pop();
                            },
                            icon: const Icon(Icons.close),
                          ),
                        )),
                    UtilityWidgets.emptyLine(),
                    ResponsiveGridCol(
                        md: 6,
                        child: Padding(
                          padding: const EdgeInsets.all(20),
                          child: Center(
                              child: Image.asset(
                            "assets/working.png",
                            width: 200,
                            height: 200,
                          )),
                        )),
                    ResponsiveGridCol(md: 6, child: aboutVacancyInfo(vacancy))
                  ],
                ),
              ));
        });
  }

  Widget aboutVacancyInfo(VacancyResponseApiModel vacancy) {
    var skills = vacancy.keySkills.map((e) => "- $e").join("\n");

    var responsibilities =
        vacancy.workingResponsibilities.map((e) => "- $e").join("\n");

    String typeOfWork = "Не заполнен";
    typeOfWorkMap.forEach((key, value) {
      if (value == vacancy.typeOfWork) {
        typeOfWork = key.replaceAll("  ", "");
      }
    });

    String workFormat = "Не заполнен";
    placementType.forEach((key, value) {
      if (value == vacancy.typeOfWorkPlacement) {
        workFormat = key.replaceAll("  ", "");
      }
    });

    String conditions = "";
    if (vacancy.companyBonuses.isEmpty) {
      conditions = "Не заполнен";
    } else {
      for (var element in vacancy.companyBonuses) {
        conditions = "$conditions \n - $element";
      }
    }
    var contactApiModel = ContactApiModel.fromJson(vacancy.contactApiModel);

    return ResponsiveGridRow(
      children: [
        UtilityWidgets.buildResponsiveGridCard(
            vacancy.vacancyName, 12, Colors.black, 25),
        UtilityWidgets.buildResponsiveGridCard(
            vacancy.companyName, 12, Colors.blueGrey, 17),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCard(
            "Город: ${vacancy.cityName}", 6, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Адрес: ${vacancy.companyFullAddress}", 6, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Заработная плата: ${vacancy.salary ?? " ~ "} ₽",
            12,
            Colors.blueGrey,
            17),
        UtilityWidgets.emptyLine(),
        UtilityWidgets.buildResponsiveGridCard(
            "Требуемые навыки:", 12, Colors.black, 20),
        UtilityWidgets.buildResponsiveGridCard(skills, 12, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Рабочие обязанности:", 12, Colors.black, 20),
        UtilityWidgets.buildResponsiveGridCard(
            responsibilities, 12, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Формат работы: $workFormat", 6, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Тип занятости работы: $typeOfWork", 6, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Что мы готовы предложить:", 12, Colors.black, 20),
        UtilityWidgets.buildResponsiveGridCard(
            conditions, 12, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Контактная информация:", 12, Colors.black, 20),
        UtilityWidgets.buildResponsiveGridCard(
            contactApiModel.fio, 12, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Телефон: ${contactApiModel.phone}", 6, Colors.blueGrey, 17),
        UtilityWidgets.buildResponsiveGridCard(
            "Email: ${contactApiModel.email}", 6, Colors.blueGrey, 17),
      ],
    );
  }

  void filterAllLoadedVacancies() {
    var cityValue = cityNameTEC.text.isEmpty ? null : cityNameTEC.text;
    var keySkillValue = keySkillTEC.text.isEmpty ? null : keySkillTEC.text;
    var typeOfWorkValue = typeTimeMap.values.first.text.isEmpty
        ? null
        : typeTimeMap.values.first.text;
    var typeOfPlacementValue = typePlacementMap.values.first.text.isEmpty
        ? null
        : typePlacementMap.values.first.text;

    if (cityValue == null &&
        keySkillValue == null &&
        typeOfWorkValue == null &&
        typeOfPlacementValue == null) {
      setState(() {
        _fellowWorkerResponseModel = _fellowWorkerService.searchAllVacancies();
      });
    } else {
      var typeWorkValue = typeOfWorkMap[typeOfWorkValue];
      var typePlacementValue = placementType[typeOfPlacementValue];

      var searchVacancyModel = SearchVacancyApiModel(
          city: cityValue,
          typeOfWork: typeWorkValue,
          typeOfWorkPlacement: typePlacementValue,
          keySkills: keySkillValue);

      setState(() {
        _fellowWorkerResponseModel =
            _fellowWorkerService.filterVacancy(searchVacancyModel);
      });
    }
  }
}

class SearchVacancies extends StatefulWidget {
  late final FellowWorkerService fellowWorkerService;

  SearchVacancies({required FellowWorkerService fWS, super.key}) {
    fellowWorkerService = fWS;
  }

  @override
  createState() => _SearchVacancies();
}
