/*
 * Copyright (c) 2-2/19/23, 11:28 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../styles/gradient_color.dart';

class _CreateVacancy extends State<CreateVacancy>
    with SingleTickerProviderStateMixin {
  late FellowWorkerService _fellowWorkerService;
  late FlutterSecureStorage _flutterSecureStorage;
  late AnimationController _animationController;

  var vacancyNameTEC = TextEditingController();
  var companyNameTEC = TextEditingController();
  var companyAddressTEC = TextEditingController();
  var cityNameTEC = TextEditingController();

  @override
  void initState() {
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
    return UtilityWidgets.buildTopBar(
        GradientEnchanted.buildGradient(buildLayout(), _animationController),
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
                    padding: const EdgeInsets.all(10.0),
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
                          companyNameTEC, "Название компании:")),
                  ResponsiveGridCol(
                      md: 6,
                      child: UtilityWidgets.buildTextField(
                          cityNameTEC, "Город:")),
                  ResponsiveGridCol(
                      md: 12,
                      child: UtilityWidgets.buildTextField(
                          companyAddressTEC, "Юридический адрес компании:"))
                ]))));
  }
}

class CreateVacancy extends StatefulWidget {
  late final FellowWorkerService fellowWorkerService;
  late final FlutterSecureStorage flutterSecureStorage;

  CreateVacancy(
      {required FellowWorkerService fWS,
      required FlutterSecureStorage fSS,
      super.key}) {
    flutterSecureStorage = fSS;
    fellowWorkerService = fWS;
  }

  @override
  createState() => _CreateVacancy();
}
