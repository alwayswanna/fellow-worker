/*
 * Copyright (c) 2-3/26/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/vacancy_request_model.dart';
import 'package:fellowworkerfront/service/account_utils.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/utils/validation_service.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../utils/value_pickers.dart';

const createVacancy = "Создание вакансии";

class _CreateVacancy extends State<CreateVacancy>
    with SingleTickerProviderStateMixin {
  late FellowWorkerService _fellowWorkerService;
  late AnimationController _animationController;

  // required skills
  var professionalSkillsFrames = <ResponsiveGridCol>[];
  var professionalSkillsTEC = <TextEditingController>[];

  // responsibilities
  var workingResponsibilitiesFrames = <ResponsiveGridCol>[];
  var workingResponsibilitiesTEC = <TextEditingController>[];

  // conditions
  var conditionsFrames = <ResponsiveGridCol>[];
  var conditionsTEC = <TextEditingController>[];

  var typePlacementMap = {uuid.v1().toString(): TextEditingController()};
  var typeTimeMap = {uuid.v1().toString(): TextEditingController()};

  var vacancyNameTEC = TextEditingController();
  var companyNameTEC = TextEditingController();
  var salaryTEC = TextEditingController();
  var companyAddressTEC = TextEditingController();
  var cityNameTEC = TextEditingController();
  var contactFullNameTEC = TextEditingController();
  var emailAddressTEC = TextEditingController();
  var phoneTEC = TextEditingController();

  @override
  void initState() {
    _fellowWorkerService = widget.fellowWorkerService;
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
    return Center(
        child: SingleChildScrollView(
            child: SizedBox(
                width: 800,
                child: ResponsiveGridRow(children: [
                  ResponsiveGridCol(
                      child: Center(
                          child: Padding(
                    padding: edgeInsets10,
                    child: Text("Создать вакансию",
                        style: UtilityWidgets.pageTitleStyle()),
                  ))),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          vacancyNameTEC, "Название вакансии:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          salaryTEC, "Заработная плата")),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          companyNameTEC, "Название компании:")),
                  ResponsiveGridCol(
                      md: 6,
                      child:
                          UtilityWidgets.buildTextField(cityNameTEC, "Город:")),
                  ResponsiveGridCol(
                      md: 12,
                      child: UtilityWidgets.buildTextField(
                          companyAddressTEC, "Юридический адрес компании:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: Text("Требуемые профессиональные навыки:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                            professionalSkillsFrames.add(buildInputSkills(
                                professionalSkillsFrames,
                                professionalSkillsTEC,
                                "Проф. навык"));
                          });
                        }, "Добавить", 15),
                      )),
                  // professional skill frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(
                              children: professionalSkillsFrames))),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: Text("Рабочие обязанности:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                            workingResponsibilitiesFrames.add(buildInputSkills(
                                workingResponsibilitiesFrames,
                                workingResponsibilitiesTEC,
                                "Что предстоит делать"));
                          });
                        }, "Добавить", 15),
                      )),
                  // professional skill frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(
                              children: workingResponsibilitiesFrames))),
                  buildDropDown("Частичная или полная занятость", typeTimeMap,
                      typeOfWorkMap),
                  buildDropDown(
                      "Офис/Удаленная", typePlacementMap, placementType),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: Text("Условия:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets8,
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                            conditionsFrames.add(buildInputSkills(
                                conditionsFrames,
                                conditionsTEC,
                                "Плюшки и бонусы компании"));
                          });
                        }, "Добавить", 15),
                      )),
                  // professional skill frames:
                  ResponsiveGridCol(
                      child: Center(
                          child:
                              ResponsiveGridRow(children: conditionsFrames))),
                  ResponsiveGridCol(
                      md: 12,
                      child: UtilityWidgets.buildTextField(
                          contactFullNameTEC, "ФИО ответственного лица:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          emailAddressTEC, "Адрес электронной почты:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          phoneTEC, "Номер телефона:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: edgeInsets10,
                        child: ElevatedButton(
                            onPressed: () {
                              sendRequestToCreateVacancy();
                            },
                            style: ElevatedButton.styleFrom(
                                shape: const RoundedRectangleBorder(
                                    borderRadius:
                                        BorderRadius.all(Radius.circular(5)))),
                            child: const Padding(
                                padding: edgeInsets10,
                                child: Text(
                                  "Создать вакансию",
                                  style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold),
                                ))),
                      ))
                ]))));
  }

  ResponsiveGridCol buildDropDown(
      String message,
      Map<String, TextEditingController> mapTec,
      Map<String, String> dropDownValues) {
    return ResponsiveGridCol(
        md: 6,
        child: StateDropdownButtonWidget(
            tec: mapTec,
            dV: dropDownValues.keys.toList(),
            id: mapTec.keys.first.toString(),
            m: message));
  }

  Future<void> sendRequestToCreateVacancy() async {
    var vacancyName = vacancyNameTEC.text;
    var companyName = companyNameTEC.text;
    var companyAddress = companyAddressTEC.text;
    var cityName = cityNameTEC.text;
    var contactFullName = contactFullNameTEC.text;
    var emailAddress = emailAddressTEC.text;
    var phone = phoneTEC.text;

    var profSkills = <String>[];
    var responsibilities = <String>[];
    var conditions = <String>[];

    if (professionalSkillsTEC.isNotEmpty) {
      for (var element in professionalSkillsTEC) {
        profSkills.add(element.text);
      }
    }

    if (workingResponsibilitiesTEC.isNotEmpty) {
      for (var element in workingResponsibilitiesTEC) {
        responsibilities.add(element.text);
      }
    }

    if (conditionsTEC.isNotEmpty) {
      for (var element in conditionsTEC) {
        conditions.add(element.text);
      }
    }

    if (ValidationUtils.validateValue([vacancyName, companyName, companyAddress, cityName, contactFullName, emailAddress, phone]) ||
        conditions.isEmpty ||
        responsibilities.isEmpty ||
        profSkills.isEmpty) {
      UtilityWidgets.dialogBuilderMessage(context, 'Заполнены не все поля');
    } else {
      var typeTime = typeTimeMap.values.first.text == typeOfWorkMap.keys.first
          ? typeOfWorkMap[typeOfWorkMap.keys.first]
          : typeOfWorkMap[typeOfWorkMap.keys.last];

      var typePlacement =
          typePlacementMap.values.first.text == placementType.keys.first
              ? placementType[placementType.keys.first]
              : placementType[placementType.keys.last];

      var vacancy = VacancyApiModel(
          vacancyId: null,
          vacancyName: vacancyName,
          salary: salaryTEC.text,
          typeOfWork: typeTime,
          typeOfWorkPlacement: typePlacement,
          companyName: companyName,
          companyFullAddress: companyAddress,
          keySkills: profSkills,
          cityName: cityName,
          workingResponsibilities: responsibilities,
          companyBonuses: conditions,
          contactApiModel: CompanyContactApiModel(
              fio: contactFullName, phone: phone, email: emailAddress));

      var response = _fellowWorkerService.createVacancy(vacancy);
      UtilityWidgets.dialogBuilderApi(
          context, response, createVacancy, '/profile');
    }
  }

  ResponsiveGridCol buildInputSkills(List<ResponsiveGridCol> frames,
      List<TextEditingController> tec, String message) {
    return ResponsiveGridCol(
        md: 12, child: buildTextFieldProfSkills(frames, tec, message));
  }

  Padding buildTextFieldProfSkills(List<ResponsiveGridCol> frames,
      List<TextEditingController> tec, String message) {
    var controller = TextEditingController();
    tec.add(controller);
    return Padding(
      padding: edgeInsets10,
      child: TextField(
        controller: controller,
        decoration: InputDecoration(
            suffixIcon: IconButton(
              icon: Icon(
                Icons.add,
                color: Theme.of(context).primaryColorDark,
              ),
              onPressed: () {
                setState(() {
                  frames.add(buildInputSkills(frames, tec, message));
                });
              },
            ),
            hintText: message,
            filled: true,
            fillColor: Colors.white,
            border:
                OutlineInputBorder(borderRadius: BorderRadius.circular(20))),
      ),
    );
  }
}

class CreateVacancy extends StatefulWidget {
  late final FellowWorkerService fellowWorkerService;

  CreateVacancy({required FellowWorkerService fWS, super.key}) {
    fellowWorkerService = fWS;
  }

  @override
  createState() => _CreateVacancy();
}
