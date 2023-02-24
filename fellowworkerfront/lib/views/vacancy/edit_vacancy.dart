/*
 * Copyright (c) 2-2/24/23, 10:12 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../models/fellow_worker_response_model.dart';
import '../../models/vacancy_request_model.dart';
import '../../service/account_utils.dart';
import '../../styles/gradient_color.dart';
import '../../utils/utility_widgets.dart';
import '../../utils/value_pickers.dart';
import '../account/edit_account_view.dart';

const editVacancy = "Редактирование вакансии";

class _EditVacancy extends State<EditVacancy>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late VacancyResponseApiModel _vacancyResponseApiModel;
  late FellowWorkerService _fellowWorkerService;
  late FlutterSecureStorage _flutterSecureStorage;
  bool isNeedInfo = true;

  var vacancyNameTEC = TextEditingController();
  var companyNameTEC = TextEditingController();
  var companyAddressTEC = TextEditingController();
  var cityNameTEC = TextEditingController();
  var contactFullNameTEC = TextEditingController();
  var emailAddressTEC = TextEditingController();
  var phoneTEC = TextEditingController();

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

  @override
  void initState() {
    _vacancyResponseApiModel = widget.vacancyResponseApiModel;
    _fellowWorkerService = widget.fellowWorkerService;
    _flutterSecureStorage = widget.flutterSecureStorage;
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
    if (isNeedInfo) {
      Future.delayed(Duration.zero, () {
        UtilityWidgets.dialogBuilderMessage(context,
            "К сожалению перечисленные поля придется заполнить заново: \n - требуемые профессиональные навыки\n - рабочие обязанности\n - условия\n - формат занятости\n - формат работы \nПриносим свои извинения 😔");
      });
      isNeedInfo = false;
    }

    return UtilityWidgets.buildTopBar(
        GradientEnchanted.buildGradient(
            buildPageLayout(),
            _animationController
        ),
        context
    );
  }

  Widget buildPageLayout() {
    var contact = ContactApiModel.fromJson(_vacancyResponseApiModel.contactApiModel);
    vacancyNameTEC.text = _vacancyResponseApiModel.vacancyName;
    companyNameTEC.text = _vacancyResponseApiModel.companyName;
    companyAddressTEC.text = _vacancyResponseApiModel.companyFullAddress;
    cityNameTEC.text = _vacancyResponseApiModel.cityName;
    contactFullNameTEC.text = contact.fio;
    emailAddressTEC.text = contact.email;
    phoneTEC.text = contact.phone;
    return Center(
        child: SingleChildScrollView(
            child: SizedBox(
                width: 800,
                child: ResponsiveGridRow(children: [
                  ResponsiveGridCol(
                      child: Center(
                          child: Padding(
                    padding: const EdgeInsets.all(10.0),
                    child: Text("Редактировать вакансию",
                        style: UtilityWidgets.pageTitleStyle()),
                  ))),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          vacancyNameTEC, "Название вакансии:")),
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
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Требуемые профессиональные навыки:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
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
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Рабочие обязанности:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
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
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Условия:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add professional skills button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
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
                        padding: padding,
                        child: ElevatedButton(
                            onPressed: () {
                              sendRequestToEditVacancy();
                            },
                            style: ElevatedButton.styleFrom(
                                shape: const RoundedRectangleBorder(
                                    borderRadius:
                                        BorderRadius.all(Radius.circular(5)))),
                            child: const Padding(
                                padding: padding,
                                child: Text(
                                  "Редактировать вакансию",
                                  style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold),
                                ))),
                      ))
                ]))));
  }

  ResponsiveGridCol buildInputSkills(
      List<ResponsiveGridCol> frames,
      List<TextEditingController> tec, String message
  ) {
    return ResponsiveGridCol(
        md: 12, child: buildTextFieldProfSkills(frames, tec, message));
  }

  ResponsiveGridCol buildDropDown(
      String message,
      Map<String, TextEditingController> mapTec,
      Map<String, String> dropDownValues
  ) {
    return ResponsiveGridCol(
        md: 6,
        child: StateDropdownButtonWidget(
            tec: mapTec,
            dV: dropDownValues.keys.toList(),
            id: mapTec.keys.first.toString(),
            m: message
        )
    );
  }

  Padding buildTextFieldProfSkills(List<ResponsiveGridCol> frames,
      List<TextEditingController> tec, String message
  ) {
    var controller = TextEditingController();
    tec.add(controller);
    return Padding(
      padding: const EdgeInsets.all(10.0),
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

  Future<void> sendRequestToEditVacancy() async {
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

    if (vacancyName.isEmpty ||
        companyName.isEmpty ||
        companyAddress.isEmpty ||
        cityName.isEmpty ||
        contactFullName.isEmpty ||
        emailAddress.isEmpty ||
        phone.isEmpty ||
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
          vacancyId: _vacancyResponseApiModel.resumeId,
          vacancyName: vacancyName,
          typeOfWork: typeTime,
          typeOfWorkPlacement: typePlacement,
          companyName: companyName,
          companyFullAddress: companyAddress,
          keySkills: profSkills,
          cityName: cityName,
          workingResponsibilities: responsibilities,
          companyBonuses: conditions,
          contactApiModel: CompanyContactApiModel(
              fio: contactFullName,
              phone: phone,
              email: emailAddress
          )
      );

      var response = _fellowWorkerService.editVacancy(
          _flutterSecureStorage,
          vacancy
      );
      UtilityWidgets.dialogBuilderApi(
          context, response, editVacancy, '/profile');
    }
  }
}

class EditVacancy extends StatefulWidget {
  late final FellowWorkerService fellowWorkerService;
  late final FlutterSecureStorage flutterSecureStorage;
  late final VacancyResponseApiModel vacancyResponseApiModel;

  EditVacancy(
      {required FellowWorkerService fWS,
      required FlutterSecureStorage fSS,
      required VacancyResponseApiModel vRAM,
      super.key}) {
    fellowWorkerService = fWS;
    flutterSecureStorage = fSS;
    vacancyResponseApiModel = vRAM;
  }

  @override
  createState() => _EditVacancy();
}
