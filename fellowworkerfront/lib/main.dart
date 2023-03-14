/*
 * Copyright (c) 1-3/9/23, 11:54 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/security/oauth2.dart';
import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/service/cv_generator_service.dart';
import 'package:fellowworkerfront/service/fellow_worker_service.dart';
import 'package:fellowworkerfront/styles/gradient_color.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/account/change_password_view.dart';
import 'package:fellowworkerfront/views/account/edit_account_view.dart';
import 'package:fellowworkerfront/views/account/profile_view.dart';
import 'package:fellowworkerfront/views/account/registration_view.dart';
import 'package:fellowworkerfront/views/main_view.dart';
import 'package:fellowworkerfront/views/resume/create_resume_view.dart';
import 'package:fellowworkerfront/views/resume/search_resume.dart';
import 'package:fellowworkerfront/views/vacancy/create_vacancy.dart';
import 'package:fellowworkerfront/views/vacancy/search_vacancies.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:url_strategy/url_strategy.dart';

const accessToken = "access_token";
const refreshToken = "refresh_token";
const refreshTokenTimeToLive = 90;

const securityStorage = FlutterSecureStorage();

void main() {
  var oauth2Service = Oauth2Service(securityStorage);
  var clientManagerService = ClientManagerService(oauth2Service);
  var fellowWorkerService = FellowWorkerService(oauth2Service);
  var cvGeneratorService = CvGeneratorService(oauth2Service);
  setPathUrlStrategy();
  runApp(MyApp(
      oauth2: oauth2Service,
      aS: clientManagerService,
      rS: fellowWorkerService,
      cG: cvGeneratorService
  ));
}

class MyApp extends StatelessWidget {
  late final Oauth2Service oauth2service;
  late final ClientManagerService clientManagerService;
  late final FellowWorkerService fellowWorkerService;
  late final CvGeneratorService cvGeneratorService;

  MyApp(
      {required Oauth2Service oauth2,
      required ClientManagerService aS,
      required FellowWorkerService rS,
      required CvGeneratorService cG,
      super.key}) {
    oauth2service = oauth2;
    clientManagerService = aS;
    fellowWorkerService = rS;
    cvGeneratorService = cG;
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fellow worker',
      routes: {
        '/registration': (context) => Registration(
              aS: clientManagerService,
            ),
        '/profile': (context) => Profile(
            aS: clientManagerService,
            rS: fellowWorkerService,
            cG: cvGeneratorService),
        '/change-password': (context) => ChangePassword(
              aS: clientManagerService,
            ),
        '/edit-account': (context) =>
            EditCurrentAccount(cM: clientManagerService),
        '/create-resume': (context) => CreateResume(fW: fellowWorkerService),
        '/create-vacancy': (context) => CreateVacancy(
              fWS: fellowWorkerService,
            ),
        '/search-vacancy': (context) =>
            SearchVacancies(fWS: fellowWorkerService),
        '/search-resume': (context) => SearchResume(
              fWS: fellowWorkerService,
            )
      },
      theme: ThemeData(
        primarySwatch: GradientEnchanted.kToDark,
      ),
      home: MyHomePage(title: 'Fellow worker', oauth2service: oauth2service),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage(
      {super.key,
      required this.title,
      required this.oauth2service});

  final String title;
  final Oauth2Service oauth2service;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Oauth2Service _oauth2service;

  @override
  void initState() {
    super.initState();
    _oauth2service = widget.oauth2service;
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 5),
      reverseDuration: const Duration(seconds: 5),
    );
  }

  Future<Widget> buildButtonProfile(ButtonStyle style) async {
    if (await _oauth2service.userContainSession()) {
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
    _oauth2service.getAccessToken().then((value) => Future.delayed(
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
    if (await _oauth2service.userContainSession()) {
      await _oauth2service.clearStorage();

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
