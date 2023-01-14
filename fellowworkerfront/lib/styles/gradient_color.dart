/*
 * Copyright (c) 1-1/17/23, 12:42 AM
 * Created by https://github.com/alwayswanna
 */

import 'package:animate_gradient/animate_gradient.dart';
import 'package:flutter/material.dart';

class GradientEnchanted {
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

  static const MaterialColor kToDark = MaterialColor(
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
}
