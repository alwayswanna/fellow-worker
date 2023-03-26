/*
 * Copyright (c) 1-3/26/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:animate_gradient/animate_gradient.dart';
import 'package:fellowworkerfront/utils/value_pickers.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../models/fellow_worker_response_model.dart';
import '../service/account_utils.dart';

const educationLevelsMap = {
  'BACHELOR' : 'Бакалавр',
  'MAGISTRACY':'Магистрант',
  'SPECIALTY':'Специалитет'
};

const edgeInsets10 = EdgeInsets.all(10);
const edgeInsets8 = EdgeInsets.all(8);

const MaterialColor kToDark = MaterialColor(
  0xff000000,
  // 0% comes in here, this will be color picked if no shade is selected when defining a Color property which doesn’t require a swatch.
  <int, Color>{
    50: Color(0xff000000), //10%
    100: Color(0xff000000), //20%
    200: Color(0xff000000), //30%
    300: Color(0xff000000), //40%
    400: Color(0xff000000), //50%
    500: Color(0xff000000), //60%
    600: Color(0xff000000), //70%
    700: Color(0xff000000), //80%
    800: Color(0xff000000), //90%
    900: Color(0xff000000), //100%
  },
);

class UtilityWidgets {

  static Widget buildGradient(Widget widget, AnimationController controller) {
    controller.forward();
    controller.repeat(reverse: true);
    return AnimateGradient(
      controller: controller,
      primaryBegin: Alignment.topLeft,
      primaryEnd: Alignment.bottomLeft,
      secondaryBegin: Alignment.bottomLeft,
      secondaryEnd: Alignment.topRight,
      primaryColors: const [
        Color.fromARGB(255, 8, 241, 184),
        Color.fromARGB(255, 117, 37, 185),
        Color.fromARGB(155, 117, 37, 185)
      ],
      secondaryColors: const [
        Color.fromARGB(155, 213, 45, 139),
        Color.fromARGB(255, 213, 45, 139),
        Color.fromARGB(255, 253, 208, 52),
      ],
      child: FutureBuilder(
        builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
          return widget;
        },
      ),
    );
  }

  static Scaffold buildTopBar(Widget bodyWidget, BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: GestureDetector(
          onTap: () {
            Navigator.pushNamed(context, "/");
          },
          child: const Text("Fellow worker"),
        )),
        body: bodyWidget);
  }

  static String extractAccountRoleDataFromWidget(String? accountRoleWidget) {
    if (accountRoleWidget == null || accountRoleWidget.isEmpty) {
      return "";
    }
    return accountTypesMap[accountRoleWidget]!;
  }

  static ResponsiveGridCol emptyLine() {
    return ResponsiveGridCol(child: const Padding(padding: EdgeInsets.all(13)));
  }

  static ResponsiveGridCol buildResponsiveGridCardNullable(String? message,
      int? md, Color fontColor, double fontSize, EdgeInsets? padding) {
    var widgetPadding = padding ?? const EdgeInsets.all(13.0);
    return ResponsiveGridCol(
        md: md,
        child: Padding(
          padding: widgetPadding,
          child: Text(message ?? "Не указано",
              style: UtilityWidgets.cardTextStyle(fontColor, fontSize)),
        ));
  }

  static ResponsiveGridCol buildResponsiveGridCard(
      String? message, int? md, Color fontColor, double fontSize) {
    return buildResponsiveGridCardNullable(message = message, md = md,
        fontColor = fontColor, fontSize = fontSize, null);
  }

  static ResponsiveGridCol buildResponsiveGridCartText(
      String? message, Color fontColor, double fontSize) {
    return buildResponsiveGridCardNullable(message = message, null,
        fontColor = fontColor, fontSize = fontSize, null);
  }

  static ResponsiveGridCol buildResponsiveGridCardWithPadding(String? message,
      int? md, Color fontColor, double fontSize, EdgeInsets padding) {
    return buildResponsiveGridCardNullable(message = message, md = md,
        fontColor = fontColor, fontSize = fontSize, padding = padding);
  }

  static Future<void> dialogBuilderApi(BuildContext context,
      Future<String> response, String message, String onOkRedirectionPath) {
    return showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(message),
          content: FutureBuilder<String>(
            future: response,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                return Text(snapshot.data!);
              } else {
                return sizedProgressiveBar(50, 50);
              }
            },
          ),
          actions: <Widget>[
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Ok'),
              onPressed: () {
                Navigator.pushNamed(context, onOkRedirectionPath);
              },
            ),
            TextButton(
              style: TextButton.styleFrom(
                textStyle: Theme.of(context).textTheme.labelLarge,
              ),
              child: const Text('Закрыть'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  static SizedBox progressiveBar() {
    return sizedProgressiveBar(100, 100);
  }

  static SizedBox sizedProgressiveBar(double width, double height) {
    return SizedBox(
        width: width,
        height: height,
        child:
            const Center(child: CircularProgressIndicator(color: Colors.cyan)));
  }

  static Future<void> dialogBuilderMessage(
      BuildContext context, String message) {
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
              child: const Text('Закрыть'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  static Widget buildWorkExperienceWidgets(
      List<String> workExperienceUuids,
      Map<String, DateTime> startTimeMap,
      Map<String, DateTime> endTimeMap,
      Map<String, TextEditingController> companyNameMap,
      Map<String, TextEditingController> positionNameMap,
      Map<String, TextEditingController> responsibilitiesMap) {
    /** creates id for group of work widgets */
    var workExperienceId = uuid.v1().toString();

    var inTime = DateTime.now();
    var outTime = DateTime.now();
    var companyName = TextEditingController();
    var positionName = TextEditingController();
    var responsibilitiesName = TextEditingController();

    /** added data to map */
    workExperienceUuids.add(workExperienceId);
    startTimeMap.addAll({workExperienceId: inTime});
    endTimeMap.addAll({workExperienceId: outTime});
    companyNameMap.addAll({workExperienceId: companyName});
    positionNameMap.addAll({workExperienceId: positionName});
    responsibilitiesMap.addAll({workExperienceId: responsibilitiesName});

    return ResponsiveGridRow(children: [
      ResponsiveGridCol(
          md: 6,
          child: Padding(
              padding: edgeInsets10,
              // создаваемое поле для DatePicker выносим в отдельный класс со State - ами и обновляем его там
              child: StateTextFieldWidget(
                tM: startTimeMap,
                id: workExperienceId,
                m: "Дата трудоустройства:",
              ))),
      ResponsiveGridCol(
          md: 6,
          child: Padding(
            padding: edgeInsets10,
            child: StateTextFieldWidget(
                tM: endTimeMap, id: workExperienceId, m: "Дата увольнения:"),
          )),
      ResponsiveGridCol(
          md: 12,
          child:
              UtilityWidgets.buildTextField(companyName, "Название компании:")),
      ResponsiveGridCol(
          md: 6,
          child: UtilityWidgets.buildTextField(positionName, "Должность:")),
      ResponsiveGridCol(
          md: 12,
          child: UtilityWidgets.buildTextField(
              responsibilitiesName, "Обязанности:"))
    ]);
  }

  static Widget buildEducationFrame(
      List<String> educationUuids,
      Map<String, DateTime> startTime,
      Map<String, DateTime> endTime,
      Map<String, TextEditingController> universityName,
      Map<String, TextEditingController> educationLevel) {
    /** creates id for group of education widgets */
    var uuidEducationFrame = uuid.v1().toString();
    var educationKeys = educationLevelMap.keys.toList();

    var startTimeEditingController = DateTime.now();
    var endTimeEditingController = DateTime.now();
    var universityNameEditingController = TextEditingController();
    var educationLevelEditingController = TextEditingController();

    /** adding widgets to map */
    educationUuids.add(uuidEducationFrame);
    startTime.addAll({uuidEducationFrame: startTimeEditingController});
    endTime.addAll({uuidEducationFrame: endTimeEditingController});
    universityName
        .addAll({uuidEducationFrame: universityNameEditingController});
    educationLevel
        .addAll({uuidEducationFrame: educationLevelEditingController});

    return ResponsiveGridRow(children: [
      ResponsiveGridCol(
          md: 6,
          child: Padding(
              padding: edgeInsets10,
              // создаваемое поле для DatePicker выносим в отдельный класс со State - ами и обновляем его там
              child: StateTextFieldWidget(
                tM: startTime,
                id: uuidEducationFrame,
                m: "Дата поступления:",
              ))),
      ResponsiveGridCol(
          md: 6,
          child: Padding(
            padding: edgeInsets10,
            child: StateTextFieldWidget(
                tM: endTime, id: uuidEducationFrame, m: "Дата окончания:"),
          )),
      ResponsiveGridCol(
          md: 12,
          child: UtilityWidgets.buildTextField(
              universityNameEditingController, "Название учебного заведения:")),
      ResponsiveGridCol(
          md: 6,
          child: StateDropdownButtonWidget(
              tec: educationLevel,
              dV: educationKeys,
              id: uuidEducationFrame,
              m: "Ученая степень:")),
    ]);
  }

  static Padding buildTextField(TextEditingController controller, String hint) {
    return Padding(
        padding: edgeInsets10,
        child: TextField(
          controller: controller,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(20)),
            hintText: hint,
          ),
        ));
  }

  static TextStyle pageTitleStyle() {
    return const TextStyle(
      color: Colors.white,
      fontSize: 35,
      fontWeight: FontWeight.bold,
      shadows: <Shadow>[
        Shadow(
          offset: Offset(10.0, 10.0),
          blurRadius: 20.0,
          color: Color.fromARGB(255, 0, 0, 0),
        )
      ],
    );
  }

  static TextStyle cardTextStyle(Color color, double fontSize) {
    return TextStyle(
      color: color,
      fontSize: fontSize,
      fontWeight: FontWeight.bold,
    );
  }

  static Widget buildCardButton(
      VoidCallback voidCallback, String message, double fontSize) {
    return buildCardButtonPadding(voidCallback, message, fontSize, 10);
  }

  static Widget buildCardButtonPadding(VoidCallback voidCallback,
      String message, double fontSize, double paddingValue) {
    return ElevatedButton(
      onPressed: () {
        voidCallback();
      },
      child: Padding(
          padding: EdgeInsets.all(paddingValue),
          child: Text(
            message,
            style: TextStyle(color: Colors.white, fontSize: fontSize),
          )),
    );
  }

  static RoundedRectangleBorder buildCardShapes() {
    return RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(20.0),
      side: const BorderSide(
        width: 2.0,
        color: Colors.black,
      ),
    );
  }

  static Card workExperienceCard(WorkExperienceResponseModel wModel) {
    return Card(
      shape: UtilityWidgets.buildCardShapes(),
      elevation: 10,
      margin: edgeInsets10,
      child: ResponsiveGridRow(
        children: [
          UtilityWidgets.buildResponsiveGridCard(
              "Дата трудоустройства: ${wModel.startTime}", 6, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Дата увольнения: ${wModel.endTime}", 6, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Компания: ${wModel.companyName}", 6, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Специальность: ${wModel.workingSpeciality}",
              6,
              Colors.black,
              15),
          UtilityWidgets.buildResponsiveGridCard(
              "Рабочие обязанности: ${wModel.responsibilities}",
              6,
              Colors.black,
              15),
        ],
      ),
    );
  }

  static Card universityCard(EducationResponseModel eModel) {
    var educationLevel = educationLevelsMap.containsKey(eModel.educationLevel) ?
        educationLevelsMap[eModel.educationLevel] : "";
    return Card(
      shape: UtilityWidgets.buildCardShapes(),
      elevation: 10,
      margin: edgeInsets10,
      child: ResponsiveGridRow(
        children: [
          UtilityWidgets.buildResponsiveGridCard(
              "Дата поступления: ${eModel.startTime}", 6, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Дата окончания: ${eModel.endTime}", 6, Colors.black, 15),
          UtilityWidgets.buildResponsiveGridCard(
              "Учебное заведение: ${eModel.educationalInstitution}",
              6,
              Colors.black,
              15),
          UtilityWidgets.buildResponsiveGridCard(
              "Ученая степень: $educationLevel", 6, Colors.black, 15),
        ],
      ),
    );
  }
}
