/*
 * Copyright (c) 1-1/17/23, 12:42 AM
 * Created by https://github.com/alwayswanna
 */

import 'package:flutter/material.dart';

import '../styles/gradient_color.dart';

class _RegistrationList extends State<Registration>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
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

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  static Widget buildLayout() {
    return const Center(
        child: SingleChildScrollView(
      child: Text("data"),
    ));
  }
}

class Registration extends StatefulWidget {
  const Registration({super.key});

  @override
  State<StatefulWidget> createState() {
    return _RegistrationList();
  }
}
