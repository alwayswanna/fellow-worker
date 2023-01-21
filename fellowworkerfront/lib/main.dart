/*
 * Copyright (c) 1-1/21/23, 11:59 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:fellowworkerfront/styles/gradient_color.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/main_vaiew.dart';
import 'package:fellowworkerfront/views/registration_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:url_strategy/url_strategy.dart';

const jwtTokenKey = "jwtToken";

void main() {
  setPathUrlStrategy();
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
  final securityStorage = const FlutterSecureStorage();

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 5),
      reverseDuration: const Duration(seconds: 5),
    );
  }

  Future<Widget> buildButtonProfile(ButtonStyle style) async {
    if (await securityStorage.containsKey(key: jwtTokenKey)) {
      return TextButton(
          onPressed: () {
             print('Profile');
          },
          style: style,
          child: const Text("Профиль"));
    } else {
      return TextButton(
          onPressed: () {
            Oauth2Service().login(securityStorage);
            setState(() {
              super.initState();
            });
          },
          style: style,
          child: const Text("Войти"));
    }
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
          FutureBuilder(
               future: buildButtonProfile(style),
              builder: (context, snapshot){
                 if(snapshot.hasData){
                   return snapshot.data!;
                 }else{
                   return TextButton(
                       onPressed: () {
                         UtilityWidgets.dialogBuilderMessage(context, "Вы не вошли в аккаунт.");
                       },
                       style: style,
                       child: const Text("Профиль"));
                 }
          }),
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
