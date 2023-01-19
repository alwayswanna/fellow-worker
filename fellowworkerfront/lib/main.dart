/*
 * Copyright (c) 1-1/19/23, 11:07 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:fellowworkerfront/styles/gradient_color.dart';
import 'package:fellowworkerfront/views/main_vaiew.dart';
import 'package:fellowworkerfront/views/registration_view.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fellow worker',
      routes: {'/registration': (context) => const Registration()},
      theme: ThemeData(
        primarySwatch: GradientEnchanted.kToDark,
      ),
      home: const MyHomePage(title: 'Fellow worker'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5),
    );
  }

  @override
  Widget build(BuildContext context) {
    final ButtonStyle style = TextButton.styleFrom(
      foregroundColor: Theme.of(context).colorScheme.onPrimary,
    );
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        actions: [
          TextButton(
              onPressed: () {
                print("message");
              },
              style: style,
              child: const Text("Профиль")),
          TextButton(
              onPressed: () {
                Oauth2Service().login();
              },
              style: style,
              child: const Text("Войти")),
          TextButton(
              onPressed: () {
                Navigator.pushNamed(context, "/registration");
              },
              style: style,
              child: const Text("Регистрация"))
        ],
      ),
      body: GradientEnchanted.buildGradient(
          const FullScreenWidget(), _animationController),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}
