/*
 * Copyright (c) 1-3/9/23, 8:15 PM
 * Created by https://github.com/alwayswanna
 */

import 'package:fellowworkerfront/service/client_manager_service.dart';
import 'package:fellowworkerfront/utils/utility_widgets.dart';
import 'package:fellowworkerfront/views/account/registration_view.dart';
import 'package:flutter/material.dart';
import 'package:responsive_grid/responsive_grid.dart';

import '../../styles/gradient_color.dart';

class _ChangePassword extends State<ChangePassword>
    with SingleTickerProviderStateMixin {
  late ClientManagerService accountService;
  var oldPasswordCont = TextEditingController();
  var newPasswordController = TextEditingController();
  var newPasswordVerifyController = TextEditingController();
  late AnimationController _animationController;
  late bool _oldPasswordVisible;
  late bool _newPasswordVisible;
  late bool _newPasswordVerifyVisible;

  @override
  void initState() {
    super.initState();
    accountService = widget.accountService;
    _oldPasswordVisible = false;
    _newPasswordVisible = false;
    _newPasswordVerifyVisible = false;
    _animationController = AnimationController(
        vsync: this,
        duration: const Duration(seconds: 5),
        reverseDuration: const Duration(seconds: 5));
  }

  @override
  Widget build(BuildContext context) {
    return UtilityWidgets.buildTopBar(
        GradientEnchanted.buildGradient(buildLayout(), _animationController),
        context
    );
  }

  Widget buildLayout() {
    return Center(
      child: SingleChildScrollView(
        child: SizedBox(
          width: 400,
          child: ResponsiveGridRow(
            children: [
              ResponsiveGridCol(
                  child: Center(
                      child: Padding(
                padding: const EdgeInsets.all(10.0),
                child: Text("Сменить пароль",
                    style: UtilityWidgets.pageTitleStyle()),
              ))),
              ResponsiveGridCol(
                  child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  controller: oldPasswordCont,
                  enableSuggestions: false,
                  obscureText: !_oldPasswordVisible,
                  autocorrect: false,
                  decoration: InputDecoration(
                      suffixIcon: IconButton(
                        icon: Icon(
                          _oldPasswordVisible
                              ? Icons.visibility
                              : Icons.visibility_off,
                          color: Theme.of(context).primaryColorDark,
                        ),
                        onPressed: () {
                          setState(() {
                            _oldPasswordVisible = !_oldPasswordVisible;
                          });
                        },
                      ),
                      hintText: "Текущий пароль:",
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(20))),
                ),
              )),
              ResponsiveGridCol(
                  child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  controller: newPasswordController,
                  enableSuggestions: false,
                  obscureText: !_newPasswordVisible,
                  autocorrect: false,
                  decoration: InputDecoration(
                      suffixIcon: IconButton(
                        icon: Icon(
                          _newPasswordVisible
                              ? Icons.visibility
                              : Icons.visibility_off,
                          color: Theme.of(context).primaryColorDark,
                        ),
                        onPressed: () {
                          setState(() {
                            _newPasswordVisible = !_newPasswordVisible;
                          });
                        },
                      ),
                      hintText: "Новый пароль:",
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(20))),
                ),
              )),
              ResponsiveGridCol(
                  child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  controller: newPasswordVerifyController,
                  enableSuggestions: false,
                  obscureText: !_newPasswordVerifyVisible,
                  autocorrect: false,
                  decoration: InputDecoration(
                      suffixIcon: IconButton(
                        icon: Icon(
                          _newPasswordVerifyVisible
                              ? Icons.visibility
                              : Icons.visibility_off,
                          color: Theme.of(context).primaryColorDark,
                        ),
                        onPressed: () {
                          setState(() {
                            _newPasswordVerifyVisible =
                                !_newPasswordVerifyVisible;
                          });
                        },
                      ),
                      hintText: "Подтвердите пароль:",
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(20))),
                ),
              )),
              ResponsiveGridCol(
                  child: Padding(
                      padding: padding,
                      child: UtilityWidgets.buildCardButton(() {
                        sendRequestCreateAccount();
                      }, "Сменить пароль", 25)))
            ],
          ),
        ),
      ),
    );
  }

  Future<void> sendRequestCreateAccount() async {
    Future<String> response;
    var oldPassword = oldPasswordCont.text;
    var newPassword = newPasswordController.text;
    var passwordVerify = newPasswordVerifyController.text;

    if (newPassword.isEmpty || passwordVerify.isEmpty || oldPassword.isEmpty) {
      response = Future<String>.value("Поля не могут быть пустыми");
    } else if (newPassword != passwordVerify) {
      response = Future<String>.value("Пароли не совпадают");
    } else {
      response = accountService.changePassword(oldPassword, newPassword);
    }
    UtilityWidgets.dialogBuilderApi(
        context, response, "Смена пароля", '/profile');
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}

class ChangePassword extends StatefulWidget {
  late final ClientManagerService accountService;

  ChangePassword(
      {
      required ClientManagerService aS,
      super.key}) {
    accountService = aS;
  }

  @override
  createState() => _ChangePassword();
}
