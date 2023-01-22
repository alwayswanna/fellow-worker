/*
 * Copyright (c) 1-1/22/23, 11:57 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:fellowworkerfront/styles/gradient_color.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/main_view.dart';
import 'package:fellowworkerfront/views/profile_view.dart';
import 'package:fellowworkerfront/views/registration_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:url_strategy/url_strategy.dart';

const jwtTokenKey = "jwtToken";

void main() {
  var securityStorage = const FlutterSecureStorage();
  setPathUrlStrategy();
  runApp(MyApp(sS: securityStorage));
}

class MyApp extends StatelessWidget {
  late FlutterSecureStorage flutterSecureStorage;

  MyApp({required FlutterSecureStorage sS, super.key}) {
    flutterSecureStorage = sS;
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fellow worker',
      routes: {
        '/registration': (context) => const Registration(),
        '/profile': (context) => Profile(sS: flutterSecureStorage)
      },
      theme: ThemeData(
        primarySwatch: GradientEnchanted.kToDark,
      ),
      home: MyHomePage(title: 'Fellow worker', flutterSS: flutterSecureStorage),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title, required this.flutterSS});

  final String title;
  final FlutterSecureStorage flutterSS;

  @override
  State<MyHomePage> createState() => _MyHomePageState(flutterSS);
}

class _MyHomePageState extends State<MyHomePage>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late FlutterSecureStorage securityStorage;

  _MyHomePageState(FlutterSecureStorage flutterSS) {
    securityStorage = flutterSS;
  }

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
            Navigator.pushNamed(context, '/profile');
          },
          style: style,
          child: const Text("Профиль"));
    } else {
      return TextButton(
          onPressed: () {
            loginAction();
          },
          style: style,
          child: const Text("Войти"));
    }
  }

  void loginAction() {
    Oauth2Service().login(securityStorage).then((value) => Future.delayed(
        const Duration(seconds: 1),
        () => {
              Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                      builder: (BuildContext context) => super.widget))
            }));
  }

  @override
  Widget build(BuildContext context) {
    final ButtonStyle style = TextButton.styleFrom(
      foregroundColor: Theme.of(context).colorScheme.onPrimary,
    );
    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          logout();
        },
        child: const Icon(Icons.exit_to_app),
      ),
      appBar: AppBar(
        title: Text(widget.title),
        actions: [
          FutureBuilder(
              future: buildButtonProfile(style),
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return snapshot.data!;
                } else {
                  return TextButton(
                      onPressed: () {
                        UtilityWidgets.dialogBuilderMessage(
                            context, "Вы не вошли в аккаунт.");
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

  void logout() async {
    if (await securityStorage.containsKey(key: jwtTokenKey)) {
      securityStorage.delete(key: jwtTokenKey);
      Future.delayed(
          const Duration(seconds: 1),
          () => {
                Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                        builder: (BuildContext context) => super.widget))
              });
      UtilityWidgets.dialogBuilderMessage(
          context, "Вы успешно вышли из аккаунта");
    } else {
      UtilityWidgets.dialogBuilderMessage(
          context, "У вас нету активной сессии");
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}
