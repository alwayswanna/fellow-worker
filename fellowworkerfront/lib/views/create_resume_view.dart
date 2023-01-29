/*
 * Copyright (c) 1-1/29/23, 12:12 AM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';
import 'package:uuid/uuid.dart';

import '../service/account_utils.dart';
import '../styles/gradient_color.dart';
import '../utils/utility_widgets.dart';

const padding = EdgeInsets.all(10);

class _CreateResume extends State<CreateResume>
    with SingleTickerProviderStateMixin {
  late final FlutterSecureStorage flutterSecureStorage;
  late final FellowWorkerService fellowWorkerService;

  late AnimationController _animationController;
  var inputSkills = <ResponsiveGridCol>[];
  var educationFrames = <ResponsiveGridCol>[];
  var profSkillsTEC = <TextEditingController>[];
  var uuid = const Uuid();

  var educationUUID = <String>[];
  var educationWidgetStartTime = <String, TextEditingController>{};
  var educationWidgetEndTime = <String, TextEditingController>{};
  var educationWidgetNameUniversity = <String, TextEditingController>{};
  var educationWidgetLeve = <String, TextEditingController>{};

  DateTime selectedDate = DateTime.now();
  var dateController = TextEditingController();
  String? educationLevel;

  // main model controllers;
  var controllerFirstName = TextEditingController();
  var controllerMiddleName = TextEditingController();
  var controllerLastName = TextEditingController();
  var controllerBirthDate = TextEditingController();
  var controllerJob = TextEditingController();
  var controllerExpectedSalary = TextEditingController();
  var controllerAbout = TextEditingController();
  var controllerBase64Image = TextEditingController();
  var controllerExtensionPostfix = TextEditingController();

  // education api model controllers;
  var controllerEduStartTime = TextEditingController();
  var controllerEduEndTime = TextEditingController();
  var controllerEduInstitution = TextEditingController();
  var controllerEduLevel = TextEditingController();

  // work experience model controllers;
  var controllerWorkStartTime = TextEditingController();
  var controllerWorkEndTime = TextEditingController();
  var controllerWorkCompanyName = TextEditingController();
  var controllerWorkSpeciality = TextEditingController();
  var controllerWorkResponsibilities = TextEditingController();

  // contact api model controllers;
  var controllerContactPhone = TextEditingController();
  var controllerContactEmail = TextEditingController();

  _CreateResume(
      {required FellowWorkerService fW, required FlutterSecureStorage fS}) {
    fellowWorkerService = fW;
    flutterSecureStorage = fS;
  }

  @override
  void initState() {
    super.initState();
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
      body:
          GradientEnchanted.buildGradient(buildLayout(), _animationController),
    );
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
                    padding: const EdgeInsets.all(10.0),
                    child: Text("Создать резюме",
                        style: UtilityWidgets.pageTitleStyle()),
                  ))),
                  // firstname:
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerFirstName, "Имя:")),
                  // middle-name
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerMiddleName, "Фамилия:")),
                  // last-name
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerLastName, "Отчество:")),
                  // job name:
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerJob, "Желаемая должность:")),
                  // expected salary
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerExpectedSalary, "Ожидаемая заработная плата:")),
                  // date of birth date:
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
                              _selectDateBirth(context);
                            }),
                      )),
                  // about:
                  ResponsiveGridCol(
                      md: 12,
                      child: UtilityWidgets.buildTextField(
                          controllerAbout, "О себе:")),
                  // professional skills title:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Профессиональные навыки:",
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
                            inputSkills.add(buildInputSkills());
                          });
                        }, "Добавить", 15),
                      )),
                  // professional skill frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(children: inputSkills))),
                  // education title:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Образование:",
                            style:
                            UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add education frame button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                              educationFrames.add(buildUniversitySectionWidgets());
                          });
                        }, "Добавить", 15),
                      )),
                  // education frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(children: educationFrames))),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: padding,
                        child: ElevatedButton(
                            onPressed: () {
                              sendRequestToCreateResume();
                            },
                            style: ElevatedButton.styleFrom(
                                shape: const RoundedRectangleBorder(
                                    borderRadius:
                                    BorderRadius.all(Radius.circular(5)))),
                            child: const Padding(
                                padding: padding,
                                child: Text(
                                  "Создать резюме",
                                  style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold),
                                ))),
                      ))
                ]))));
  }

  Future<void> sendRequestToCreateResume() async {
    // TODO: implement logic to send request;
    for (var element in profSkillsTEC) {
      print("element: $element");
    }
  }

  ResponsiveGridCol buildInputSkills() {
    return ResponsiveGridCol(
        md: 12, child: buildTextFieldProfSkills(controllerLastName));
  }

  Padding buildTextFieldProfSkills(TextEditingController controller) {
    var skillController = TextEditingController();
    profSkillsTEC.add(skillController);
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: TextField(
        controller: skillController,
        decoration: InputDecoration(
            suffixIcon: IconButton(
              icon: Icon(
                Icons.add,
                color: Theme.of(context).primaryColorDark,
              ),
              onPressed: () {
                setState(() {
                  inputSkills.add(buildInputSkills());
                });
              },
            ),
            hintText: "Навык:",
            filled: true,
            fillColor: Colors.white,
            border:
                OutlineInputBorder(borderRadius: BorderRadius.circular(20))),
      ),
    );
  }

  Future<void> _selectDateBirth(BuildContext context) async {
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

  ResponsiveGridCol buildUniversitySectionWidgets(){
    return ResponsiveGridCol(md: 12, child: buildEducationFrame());
  }

  Widget buildEducationFrame(){
    // TODO: create controllers logic with UUID;
    var uuidEducationFrame = uuid.v1();
    educationUUID.add(uuidEducationFrame.toString());
    return ResponsiveGridRow(children: [
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
                  " Дата поступления: ${selectedDate.year}/${selectedDate.month}/${selectedDate.day}",
                ),
                readOnly: true,
                // when true user cannot edit text
                onTap: () async {
                  _selectDateBirth(context);
                }),
          )),
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
                  " Дата окончания: ${selectedDate.year}/${selectedDate.month}/${selectedDate.day}",
                ),
                readOnly: true,
                // when true user cannot edit text
                onTap: () async {
                  _selectDateBirth(context);
                }),
          )),
      ResponsiveGridCol(
          md: 12,
          child: UtilityWidgets.buildTextField(
              controllerFirstName, "Название учебного заведения:")),
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
                    hint: const Text("  Ученая степень:"),
                    value: educationLevel,
                    onChanged: (value) {
                      setState(() {
                        educationLevel = value!;
                      });
                    },
                    items: educationLevels.map((String text) {
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
    ]);
  }
}

class CreateResume extends StatefulWidget {
  late final FlutterSecureStorage flutterSecureStorage;
  late final FellowWorkerService fellowWorkerService;

  CreateResume(
      {required FlutterSecureStorage fS,
      required FellowWorkerService fW,
      super.key}) {
    flutterSecureStorage = fS;
    fellowWorkerService = fW;
  }

  @override
  State<StatefulWidget> createState() {
    return _CreateResume(fW: fellowWorkerService, fS: flutterSecureStorage);
  }
}
