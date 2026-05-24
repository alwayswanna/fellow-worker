import 'package:flutter/material.dart';
import 'package:flutter_web_plugins/url_strategy.dart';

import 'screen/admin/admin_screen.dart';
import 'screen/login_screen.dart';
import 'screen/profile_screen.dart';
import 'screen/register_screen.dart';
import 'service/admin_api_service.dart';
import 'service/auth_service.dart';
import 'service/resume_api_service.dart';
import 'service/user_api_service.dart';
import 'service/vacancy_api_service.dart';

// Services are singletons created once in main() and shared across the app.
late final AuthService _authService;
late final UserApiService _userApiService;
late final AdminApiService _adminApiService;
late final ResumeApiService _resumeApiService;
late final VacancyApiService _vacancyApiService;

void main() {
  _authService = AuthService();
  _userApiService = UserApiService(_authService);
  _adminApiService = AdminApiService(_authService);
  _resumeApiService = ResumeApiService(_authService);
  _vacancyApiService = VacancyApiService(_authService);

  usePathUrlStrategy();
  runApp(const FellowWorkerApp());
}

class FellowWorkerApp extends StatelessWidget {
  const FellowWorkerApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fellow Worker',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      onGenerateInitialRoutes: (_) => [
        MaterialPageRoute(
          builder: (context) => AppRouter(
            authService: _authService,
            userApiService: _userApiService,
          ),
        ),
      ],
      onUnknownRoute: (settings) => MaterialPageRoute(
        builder: (context) => AppRouter(
          authService: _authService,
          userApiService: _userApiService,
        ),
      ),
      routes: {
        '/login': (context) => LoginScreen(
              authService: _authService,
              userApiService: _userApiService,
            ),
        '/register': (context) => RegisterScreen(userApiService: _userApiService),
        '/admin': (context) => AdminScreen(
              adminApiService: _adminApiService,
              vacancyApiService: _vacancyApiService,
              resumeApiService: _resumeApiService,
            ),
        '/profile': (context) => ProfileScreen(
              authService: _authService,
              userApiService: _userApiService,
              resumeApiService: _resumeApiService,
              vacancyApiService: _vacancyApiService,
            ),
      },
    );
  }
}

/// Determines initial route: handles OAuth2 callback or restores existing session.
class AppRouter extends StatefulWidget {
  final AuthService authService;
  final UserApiService userApiService;

  const AppRouter({
    super.key,
    required this.authService,
    required this.userApiService,
  });

  @override
  State<AppRouter> createState() => _AppRouterState();
}

class _AppRouterState extends State<AppRouter> {
  @override
  void initState() {
    super.initState();
    _resolveInitialRoute();
  }

  Future<void> _resolveInitialRoute() async {
    final uri = Uri.base;
    final code = uri.queryParameters['code'];
    final state = uri.queryParameters['state'];

    if (code != null && state != null) {
      final success = await widget.authService.handleCallback(code, state);
      _navigate(success ? '/profile' : '/login');
      return;
    }

    final authenticated = await widget.authService.isAuthenticated();
    _navigate(authenticated ? '/profile' : '/login');
  }

  void _navigate(String route) {
    if (mounted) Navigator.of(context).pushReplacementNamed(route);
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(body: Center(child: CircularProgressIndicator()));
  }
}
