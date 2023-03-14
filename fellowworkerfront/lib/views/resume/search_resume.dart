/*
 * Copyright (c) 3-3/8/23, 10:19 AM
 * Created by https://github.com/alwayswanna
 */
import 'package:fellowworkerfront/models/fellow_worker_response_model.dart';
import 'package:fellowworkerfront/models/search_resume_model.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../styles/gradient_color.dart';
import '../../utils/utility_widgets.dart';
import '../account/edit_account_view.dart';

class _StateResume extends State<SearchResume>
    with SingleTickerProviderStateMixin {
  late FellowWorkerService _fellowWorkerService;
  late AnimationController _animationController;
  late Future<FellowWorkerResponseModel> _response;

  var cityNameTEC = TextEditingController();
  var keySkillTEC = TextEditingController();
  var jobTitleTEC = TextEditingController();
  var salaryTEC = TextEditingController();

  @override
  void initState() {
    _fellowWorkerService = widget._fellowWorkerService;
    _response = _fellowWorkerService.searchAllResume();
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
        ResponsiveGridCol(md: 9, child: buildResumesView()),
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
                  "Специальность:", Colors.blueGrey, 10),
              ResponsiveGridCol(
                  child: UtilityWidgets.buildTextField(
                      jobTitleTEC, "Специальность:")),
              UtilityWidgets.emptyLine(),
              UtilityWidgets.buildResponsiveGridCartText(
                  "Заработная плата:", Colors.blueGrey, 10),
              ResponsiveGridCol(
                  child: UtilityWidgets.buildTextField(
                      jobTitleTEC, "Заработная плата менее:")),
              UtilityWidgets.emptyLine(),
              ResponsiveGridCol(
                  child: Padding(
                      padding: const EdgeInsets.all(8),
                      child: UtilityWidgets.buildCardButton(() {
                        filterAllLoadedResume();
                      }, "Искать", 15)))
            ],
          ),
        )));
  }

  void filterAllLoadedResume() {
    var cityValue = cityNameTEC.text.isEmpty ? null : cityNameTEC.text;
    var skillValue = keySkillTEC.text.isEmpty ? null : keySkillTEC.text;
    var salaryValue = salaryTEC.text.isEmpty ? null : salaryTEC.text;
    var jobValue = jobTitleTEC.text.isEmpty ? null : jobTitleTEC.text;

    var searchResumeModel = SearchResumeApiModel(
        city: cityValue,
        keySkills: skillValue,
        job: jobValue,
        salary: salaryValue);

    setState(() {
      _response = _fellowWorkerService.filterResume(searchResumeModel);
    });
  }

  Widget buildResumesView() {
    return FutureBuilder(
        future: _response,
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
    var vacancies = fellowWorkerResponseModel.resumes;
    var columns = <ResponsiveGridCol>[];
    if (vacancies != null && vacancies.isNotEmpty) {
      for (var v in vacancies) {
        var salary = v.expectedSalary ?? " ~ ";
        columns.add(ResponsiveGridCol(
            child: Card(
          shape: UtilityWidgets.buildCardShapes(),
          elevation: 10,
          margin: const EdgeInsets.all(20),
          child: ResponsiveGridRow(
            children: [
              UtilityWidgets.buildResponsiveGridCard(
                  v.job, 12, Colors.black, 20),
              UtilityWidgets.buildResponsiveGridCard(
                  "${v.lastName} ${v.firstName} ${v.middleName}",
                  2,
                  Colors.grey,
                  13),
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
                      buildAboutResume(v, context);
                    }, "Подробнее", 13),
                  ))
            ],
          ),
        )));
      }
    }

    return ResponsiveGridRow(children: columns);
  }

  Future<void> buildAboutResume(
      ResumeResponseModel resume, BuildContext context) {
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
                    ResponsiveGridCol(md: 6, child: aboutResumeInfo(resume)),
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
                  ],
                ),
              ));
        });
  }

  Widget aboutResumeInfo(ResumeResponseModel resume) {
    var professionalSkills = resume.professionalSkills.join("\n");
    var contact = ContactResponseModel.fromJson(resume.contact);

    var resumeDateUpdate = DateTime.parse(resume.lastUpdate);
    var viewDateTime =
        "${DateFormat.yMMMd().format(resumeDateUpdate)} ${DateFormat.Hm().format(resumeDateUpdate)}";

    return ResponsiveGridRow(children: [
      UtilityWidgets.buildResponsiveGridCard(
          "${resume.middleName} ${resume.firstName} ${resume.lastName}",
          12,
          Colors.black,
          25),
      UtilityWidgets.buildResponsiveGridCard(resume.job, 12, Colors.black, 25),
      UtilityWidgets.buildResponsiveGridCard(
          "Дата рождения: ${resume.birthDate}", 12, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Город: ${resume.city}", 6, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Ожидаемая заработная плата: ${resume.expectedSalary} ₽",
          6,
          Colors.blueGrey,
          15),
      UtilityWidgets.buildResponsiveGridCard(
          "Контактные данные:", 12, Colors.black, 25),
      UtilityWidgets.buildResponsiveGridCard(
          "Email адрес: ${contact.email}", 6, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Номер телефона: ${contact.phone}", 6, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Личные качества:", 12, Colors.black, 20),
      UtilityWidgets.buildResponsiveGridCard(
          resume.about ?? "Данные не заполнены", 12, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Профессиональные навыки:", 12, Colors.black, 20),
      UtilityWidgets.buildResponsiveGridCard(
          professionalSkills, 12, Colors.blueGrey, 15),
      UtilityWidgets.buildResponsiveGridCard(
          "Образование:", 12, Colors.black, 20),
      buildEducation(resume),
      UtilityWidgets.buildResponsiveGridCard(
          "Опыт работы:", 12, Colors.black, 20),
      buildWorkExperience(resume),
      UtilityWidgets.buildResponsiveGridCard(
          "Последнее обновление: $viewDateTime", 12, Colors.blueGrey, 15),
      UtilityWidgets.emptyLine(),
    ]);
  }

  ResponsiveGridCol buildWorkExperience(ResumeResponseModel resume) {
    if (resume.workingHistory == null) {
      return UtilityWidgets.buildResponsiveGridCard(
          "Без опыта работы", 12, Colors.blueGrey, 15);
    }

    var widgets = <ResponsiveGridCol>[];
    resume.workingHistory?.forEach((element) {
      var workModel = WorkExperienceResponseModel.fromJson(element);
      widgets.add(ResponsiveGridCol(
          child: UtilityWidgets.workExperienceCard(workModel)));
    });

    return ResponsiveGridCol(
        child: Center(
      child: SingleChildScrollView(
        child: ResponsiveGridRow(children: widgets),
      ),
    ));
  }

  ResponsiveGridCol buildEducation(ResumeResponseModel resume) {
    if (resume.education == null) {
      return UtilityWidgets.buildResponsiveGridCard(
          "Данные не указаны", 12, Colors.blueGrey, 15);
    }

    var widgets = <ResponsiveGridCol>[];
    resume.education?.forEach((element) {
      var educationModel = EducationResponseModel.fromJson(element);
      widgets.add(ResponsiveGridCol(
          child: UtilityWidgets.universityCard(educationModel)));
    });
    return ResponsiveGridCol(
        child: Center(
            child: SingleChildScrollView(
                child: ResponsiveGridRow(children: widgets))));
  }
}

class SearchResume extends StatefulWidget {
  late final FellowWorkerService _fellowWorkerService;

  SearchResume({required FellowWorkerService fWS, super.key}) {
    _fellowWorkerService = fWS;
  }

  @override
  createState() => _StateResume();
}
