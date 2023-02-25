/*
 * Copyright (c) 2-2/23/23, 10:10 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter/material.dart';

class StateTextFieldWidget extends StatefulWidget {
  late final Map<String, DateTime> timeMap;
  late final String uuidWidget;
  late final String message;

  StateTextFieldWidget({super.key,
    required Map<String, DateTime> tM,
    required String id,
    required String m}) {
    timeMap = tM;
    uuidWidget = id;
    message = m;
  }

  @override
  createState() => _StateTextFieldWidget();
}

class _StateTextFieldWidget extends State<StateTextFieldWidget> {
  late Map<String, DateTime> timeMap;
  late String uuidWidget;
  late String message;

  @override
  void initState() {
    timeMap = widget.timeMap;
    uuidWidget = widget.uuidWidget;
    message = widget.message;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return TextField(
        decoration: InputDecoration(
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(20)),
            hintText:
            " $message ${timeMap[uuidWidget]!.year}/${timeMap[uuidWidget]!
                .month}/${timeMap[uuidWidget]!.day}"),
        readOnly: true,
        // when true user cannot edit text
        onTap: () async {
          _selectDateOfEducation(context, uuidWidget, timeMap);
        });
  }

  Future<void> _selectDateOfEducation(BuildContext context, String valueId,
      Map<String, DateTime> mapController) async {
    DateTime? picked = await showDatePicker(
        helpText: "Выбранная дата",
        fieldLabelText: "Выберете дату: ",
        context: context,
        initialDate: mapController[valueId]!,
        firstDate: DateTime(1900, 8),
        lastDate: DateTime(2101));
    if (picked != null && picked != mapController[valueId]!) {
      setState(() {
        mapController[valueId] = picked;
      });
    }
  }
}

class StateDropdownButtonWidget extends StatefulWidget {
  late final Map<String, TextEditingController> textEditingContMap;
  late final List<String> dropDownValues;
  late final String uuidWidget;
  late final String message;

  StateDropdownButtonWidget({super.key,
    required Map<String, TextEditingController> tec,
    required List<String> dV,
    required String id,
    required String m}) {
    textEditingContMap = tec;
    dropDownValues = dV;
    uuidWidget = id;
    message = m;
  }

  @override
  createState() => _StateDropdownButtonWidget();
}

class _StateDropdownButtonWidget extends State<StateDropdownButtonWidget> {
  late final Map<String, TextEditingController> textEditingContMap;
  late final List<String> dropDownValues;
  late final String uuidWidget;
  late final String message;

  @override
  void initState() {
    textEditingContMap = widget.textEditingContMap;
    uuidWidget = widget.uuidWidget;
    message = widget.message;
    dropDownValues = widget.dropDownValues;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        margin: const EdgeInsets.all(10.0),
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20.0),
            border: Border.all(color: Colors.black)),
        child: DropdownButtonHideUnderline(
          child: DropdownButton<String>(
              hint: Text("  $message"),
              value: textEditingContMap[uuidWidget]!.text.isEmpty
                  ? null
                  : textEditingContMap[uuidWidget]!.text,
              onChanged: (value) {
                setState(() {
                  textEditingContMap[uuidWidget]!.text = value!;
                });
              },
              items: buildDropDownButtonsForEducationFrame()),
        ));
  }

  List<DropdownMenuItem<String>> buildDropDownButtonsForEducationFrame() {
    List<DropdownMenuItem<String>> buttons = [];
    for (var level in dropDownValues) {
      buttons.add(
          DropdownMenuItem(
              value: level,
              child: Text(
                level,
                style: const TextStyle(color: Colors.black),
              ))
      );
    }

    return buttons;
  }
}
