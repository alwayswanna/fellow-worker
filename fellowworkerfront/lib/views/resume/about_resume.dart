/*
 * Copyright (c) 2-3/9/23, 8:15 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/models/fellow_worker_response_model.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/views/resume/edit_resume.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../styles/gradient_color.dart';
import '../../utils/utility_widgets.dart';

class _AboutResume extends State<AboutResume>
    with SingleTickerProviderStateMixin {
  late ResumeResponseModel _resumeResponseModel;
  late FellowWorkerService _fellowWorkerService;
  late AnimationController _animationController;

  @override
  void initState() {
    _resumeResponseModel = widget.resumeResponseModel;
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
        GradientEnchanted.buildGradient(
            buildPageLayout(), _animationController),
        context);
  }

  Widget buildPageLayout() {
    return Padding(
        padding: const EdgeInsets.all(8.0),
        child: Center(
            child: SingleChildScrollView(
                child: ResponsiveGridRow(children: [
          ResponsiveGridCol(
              child: Center(
            child: Padding(
                padding: const EdgeInsets.fromLTRB(10, 10, 10, 50),
                child: Text("Ваше активное резюме",
                    style: UtilityWidgets.pageTitleStyle())),
          )),
          ResponsiveGridCol(
              child: Card(
                  shape: UtilityWidgets.buildCardShapes(),
                  elevation: 10,
                  margin: const EdgeInsets.all(20),
                  child: buildResumeCardWidget()))
        ]))));
  }

  Widget buildResumeCardWidget() {
    var textPadding = const EdgeInsets.fromLTRB(13, 0, 13, 3);
    var resumeDateUpdate = DateTime.parse(_resumeResponseModel.lastUpdate);
    var viewDateTime =
        "${DateFormat.yMMMd().format(resumeDateUpdate)} ${DateFormat.Hm().format(resumeDateUpdate)}";
    var contactModel =
        ContactResponseModel.fromJson(_resumeResponseModel.contact);
    var professionalSkills = _resumeResponseModel.professionalSkills.join("\n");
    return Center(
      child: ResponsiveGridRow(
        children: [
          UtilityWidgets.buildResponsiveGridCard(
              _resumeResponseModel.job, 10, Colors.black, 25),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "ФИО:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Дата рождения:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "${_resumeResponseModel.middleName} ${_resumeResponseModel.firstName} ${_resumeResponseModel.lastName}",
              6,
              Colors.black,
              15,
              textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              _resumeResponseModel.birthDate, 6, Colors.black, 15, textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Желаемая должность:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Желаемая зарплата:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              _resumeResponseModel.job, 6, Colors.black, 15, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "${_resumeResponseModel.expectedSalary} ₽",
              6,
              Colors.black,
              15,
              textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Email адрес:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Номер телефона:", 6, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              contactModel.email, 6, Colors.black, 15, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              contactModel.phone, 6, Colors.black, 15, textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Город:", 12, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              _resumeResponseModel.city ?? "",
              12,
              Colors.black,
              15,
              textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Обо мне:", 12, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              _resumeResponseModel.about, 6, Colors.black, 15, textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              "Профессиональные навыки:", 12, Colors.blueGrey, 10, textPadding),
          UtilityWidgets.buildResponsiveGridCardWithPadding(
              professionalSkills, 12, Colors.black, 15, textPadding),
          UtilityWidgets.emptyLine(),
          UtilityWidgets.buildResponsiveGridCard(
              "Образование:", 12, Colors.blueGrey, 15),
          buildEducation(),
          UtilityWidgets.buildResponsiveGridCard(
              "Опыт работы:", 12, Colors.blueGrey, 15),
          buildWorkExperience(),
          UtilityWidgets.buildResponsiveGridCard(
              "Последнее обновление: $viewDateTime", 12, Colors.black, 15),
          UtilityWidgets.emptyLine(),
          ResponsiveGridCol(
              md: 2,
              child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: UtilityWidgets.buildCardButtonPadding(() {
                    Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (context) => EditResume(
                              fWS: _fellowWorkerService,
                              rM: _resumeResponseModel,
                                )));
                  }, "Редактировать", 18.0, 13.0))),
          ResponsiveGridCol(
              md: 2,
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: UtilityWidgets.buildCardButtonPadding(() {
                  UtilityWidgets.dialogBuilderApi(
                      context,
                      _fellowWorkerService.deleteResume(
                          _resumeResponseModel.resumeId
                      ),
                      "Удалить резюме",
                      "/profile");
                }, "Удалить", 18.0, 13.0),
              ))
        ],
      ),
    );
  }

  ResponsiveGridCol buildEducation() {
    if (_resumeResponseModel.education == null) {
      return UtilityWidgets.buildResponsiveGridCard(
          "Данные не указаны", 12, Colors.black, 15);
    }

    var widgets = <ResponsiveGridCol>[];
    _resumeResponseModel.education?.forEach((element) {
      var educationModel = EducationResponseModel.fromJson(element);
      widgets.add(ResponsiveGridCol(child: UtilityWidgets.universityCard(educationModel)));
    });
    return ResponsiveGridCol(
        child: Center(
            child: SingleChildScrollView(
                child: ResponsiveGridRow(children: widgets))));
  }

  ResponsiveGridCol buildWorkExperience() {
    if (_resumeResponseModel.workingHistory == null) {
      return UtilityWidgets.buildResponsiveGridCard(
          "Без опыта работы", 12, Colors.black, 15);
    }

    var widgets = <ResponsiveGridCol>[];
    _resumeResponseModel.workingHistory?.forEach((element) {
      var workModel = WorkExperienceResponseModel.fromJson(element);
      widgets.add(ResponsiveGridCol(child: UtilityWidgets.workExperienceCard(workModel)));
    });

    return ResponsiveGridCol(
        child: Center(
      child: SingleChildScrollView(
        child: ResponsiveGridRow(children: widgets),
      ),
    ));
  }
}

class AboutResume extends StatefulWidget {
  late final ResumeResponseModel resumeResponseModel;
  late final FellowWorkerService fellowWorkerService;

  AboutResume({
    required ResumeResponseModel resume,
    required FellowWorkerService fS,
    super.key})
  {
    resumeResponseModel = resume;
    fellowWorkerService = fS;
  }

  @override
  createState() => _AboutResume();
}
