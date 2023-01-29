/*
 * Copyright (c) 1-2/19/23, 11:28 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/resume_request_model.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../service/account_utils.dart';
import '../../service/validation_service.dart';
import '../../styles/gradient_color.dart';
import '../../utils/utility_widgets.dart';

const padding = EdgeInsets.all(10);
const String createResume = "Создание резюме";

class _CreateResume extends State<CreateResume> with SingleTickerProviderStateMixin {
  late FlutterSecureStorage _flutterSecureStorage;
  late FellowWorkerService _fellowWorkerService;
  late AnimationController _animationController;

  var skillsFrames = <ResponsiveGridCol>[];
  var educationFrames = <ResponsiveGridCol>[];
  var workExperienceFrames = <ResponsiveGridCol>[];
  var professionalSkillsTec = <TextEditingController>[];
  PlatformFile? platformFile;

  var educationHistoryUUIDs = <String>[];
  var educationWidgetStartTime = <String, DateTime>{};
  var educationWidgetEndTime = <String, DateTime>{};
  var educationWidgetNameUniversity = <String, TextEditingController>{};
  var educationWidgetLevel = <String, TextEditingController>{};

  var workExperienceUUIDs = <String>[];
  var workInStartTime = <String, DateTime>{};
  var workOutEndTime = <String, DateTime>{};
  var companyNameTec = <String, TextEditingController>{};
  var positionNameTec = <String, TextEditingController>{};
  var responsibilitiesTec = <String, TextEditingController>{};

  DateTime selectedBirthDate = DateTime.now();
  var birthDateController = TextEditingController();
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

  // contact api model controllers;
  var controllerContactPhone = TextEditingController();
  var controllerContactEmail = TextEditingController();

  @override
  void initState() {
    super.initState();
    _fellowWorkerService = widget.workerService;
    _flutterSecureStorage = widget.securityStorage;
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
        GradientEnchanted.buildGradient(buildLayout(), _animationController),
        context
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
                          controllerExpectedSalary,
                          "Ожидаемая заработная плата:")),
                  // date of birth date:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(10),
                        child: TextField(
                            controller: birthDateController,
                            //editing controller of this TextField
                            decoration: InputDecoration(
                              filled: true,
                              fillColor: Colors.white,
                              border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(20)),
                              hintText:
                                  " Дата рождения: ${selectedBirthDate.year}/${selectedBirthDate.month}/${selectedBirthDate.day}",
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
                            skillsFrames.add(buildInputSkills());
                          });
                        }, "Добавить", 15),
                      )),
                  // professional skill frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(children: skillsFrames))),
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
                            educationFrames
                                .add(buildUniversitySectionWidgets());
                          });
                        }, "Добавить", 15),
                      )),
                  // education frames:
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(children: educationFrames))),
                  // work experience frames
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Опыт работы:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  // add work experience button:
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                            workExperienceFrames
                                .add(buildWorkExperienceFrame());
                          });
                        }, "Добавить", 15),
                      )),
                  ResponsiveGridCol(
                      child: Center(
                          child: ResponsiveGridRow(
                              children: workExperienceFrames))),

                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text("Контактная информация:",
                            style:
                                UtilityWidgets.cardTextStyle(Colors.white, 20)),
                      )),
                  ResponsiveGridCol(md: 6, child: Container()),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerContactEmail, "Адрес эл. почты:")),
                  // last-name
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          controllerContactPhone, "Номер телефона:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: UtilityWidgets.buildCardButton(() {
                          setState(() {
                            _attachResumeFilePhone();
                          });
                        }, "Прикрепить фото", 15),
                      )),
                  ResponsiveGridCol(md: 6, child: Container()),
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

  ResponsiveGridCol buildInputSkills() {
    return ResponsiveGridCol(
        md: 12, child: buildTextFieldProfSkills(controllerLastName));
  }

  Padding buildTextFieldProfSkills(TextEditingController controller) {
    var skillController = TextEditingController();
    professionalSkillsTec.add(skillController);
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
                  skillsFrames.add(buildInputSkills());
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
        helpText: "Выбранная дата",
        fieldLabelText: "Выберете дату: ",
        context: context,
        initialDate: selectedBirthDate,
        firstDate: DateTime(1900, 8),
        lastDate: DateTime(2101));
    if (picked != null && picked != selectedBirthDate) {
      setState(() {
        selectedBirthDate = picked;
      });
    }
  }

  /// build education widgets
  ResponsiveGridCol buildUniversitySectionWidgets() {
    return ResponsiveGridCol(
        md: 12,
        child: UtilityWidgets.buildEducationFrame(
            educationHistoryUUIDs,
            educationWidgetStartTime,
            educationWidgetEndTime,
            educationWidgetNameUniversity,
            educationWidgetLevel));
  }

  /// build education widgets.
  ResponsiveGridCol buildWorkExperienceFrame() {
    return ResponsiveGridCol(
        md: 12,
        child: UtilityWidgets.buildWorkExperienceWidgets(
            workExperienceUUIDs,
            workInStartTime,
            workOutEndTime,
            companyNameTec,
            positionNameTec,
            responsibilitiesTec));
  }

  Future<void> _attachResumeFilePhone() async {
    FilePickerResult? result = await FilePicker.platform
        .pickFiles(type: FileType.image, allowMultiple: false);
    if (result != null) {
      setState(() {
        platformFile = result.files.first;
      });
    } else {
      return UtilityWidgets.dialogBuilderMessage(
          context, "Вы не выбрали изображение");
    }
  }

  Future<void> sendRequestToCreateResume() async {
    var firstName = controllerFirstName.text;
    var middleName = controllerMiddleName.text;
    var lastName = controllerLastName.text;
    var job = controllerJob.text;
    var expectedSalary = controllerExpectedSalary.text;
    var about = controllerAbout.text;
    var contactPhone = controllerContactPhone.text;
    var contactEmail = controllerContactEmail.text;

    String? requestDate = ValidationService.isValidBirthDate(selectedBirthDate)
        ? selectedBirthDate.toIso8601String()
        : null;

    if (firstName.isEmpty ||
        middleName.isEmpty ||
        lastName.isEmpty ||
        job.isEmpty ||
        expectedSalary.isEmpty ||
        about.isEmpty ||
        contactEmail.isEmpty ||
        contactPhone.isEmpty ||
        requestDate == null) {
      UtilityWidgets.dialogBuilderMessage(context, 'Заполнены не все поля');
    } else {
      // Build professional skills:
      List<String> professionalSkills = [];
      for (var element in professionalSkillsTec) {
        var currentValue = element.text;
        if (currentValue.isNotEmpty) {
          professionalSkills.add(currentValue);
        }
      }

      // Build education request:
      List<EducationApiModel> education = [];
      for (var institution in educationHistoryUUIDs) {
        var selectedLvl = educationWidgetLevel[institution]!.text;
        String level;
        if (selectedLvl == educationLevels[0]) {
          level = specialist;
        } else if (selectedLvl == educationLevels[1]) {
          level = undergraduate;
        } else {
          level = master;
        }

        education.add(EducationApiModel(
            startTime: educationWidgetStartTime[institution]!.toIso8601String(),
            endTime: educationWidgetEndTime[institution]!.toIso8601String(),
            educationalInstitution:
                educationWidgetNameUniversity[institution]!.text,
            educationLevel: level));
      }

      // Build working history request:
      List<WorkExperienceApiModel> workExperience = [];
      for (var work in workExperienceUUIDs) {
        workExperience.add(WorkExperienceApiModel(
            startTime: workInStartTime[work]!.toIso8601String(),
            endTime: workOutEndTime[work]!.toIso8601String(),
            companyName: companyNameTec[work]!.text,
            workingSpecialty: positionNameTec[work]!.text,
            responsibilities: responsibilitiesTec[work]!.text));
      }

      var requestBodyMessage = ResumeApiModel(
          resumeId: null,
          firstName: firstName,
          middleName: middleName,
          lastName: lastName,
          birthDate: requestDate,
          job: job,
          expectedSalary: expectedSalary,
          about: about,
          education: education.isNotEmpty ? education : null,
          professionalSkills:
              professionalSkills.isNotEmpty ? professionalSkills : null,
          workingHistory: workExperience.isNotEmpty ? workExperience : null,
          contact:
              ContactResumeApiModel(phone: contactPhone, email: contactEmail),
          base64Image: RequestUtils.buildBase64Image(platformFile),
          extensionPostfix: RequestUtils.buildExtension(platformFile));
      var response = _fellowWorkerService.createResume(
          _flutterSecureStorage, requestBodyMessage);
      UtilityWidgets.dialogBuilderApi(
          context, response, createResume, '/profile');
    }
  }
}

class CreateResume extends StatefulWidget {
  late final FlutterSecureStorage securityStorage;
  late final FellowWorkerService workerService;

  CreateResume(
      {required FlutterSecureStorage fS,
      required FellowWorkerService fW,
      super.key}) {
    securityStorage = fS;
    workerService = fW;
  }

  @override
  createState() => _CreateResume();
}
