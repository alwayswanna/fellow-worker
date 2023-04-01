/*
 * Copyright (c) 07-4/1/23, 2:29 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.controller;

import a.gleb.clientmanager.BaseClientManagerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseClientManagerTest {

    @Test
    @DisplayName("Create new account (employee).")
    void successfullyCreateNewAccountForEmployeeTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ваш аккаунт успешно создан"));
    }

    @Test
    @DisplayName("Create new account (company).")
    void successfullyCreateNewAccountForCompanyTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ваш аккаунт успешно создан"));
    }

    @Test
    @DisplayName("Successfully change account password.")
    void successfullyChangePasswordTest() throws Exception {
        /* prepare data */
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(put(buildPath.apply("/change-password"))
                        .content("""
                                {
                                  "oldPassword": "TestPassword",
                                  "newPassword": "new-password"
                                }
                                """)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ваш пароль успешно изменен"));

        var afterTestAccount = repository.findAccountById(savedAccount.getId());
        assertTrue(afterTestAccount.isPresent());
        assertNotEquals(afterTestAccount.get().getPassword(), savedAccount.getPassword());
    }

    @Test
    @DisplayName("Successfully edit existing account.")
    void successfullyChangeExistingAccountDataTest() throws Exception {
        /* prepare data */
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(put(buildPath.apply("/edit"))
                        .content("""
                                        {
                                           "username": "username32",
                                           "email": "olegIv@mail.ru",
                                           "firstName": "Петр",
                                           "middleName": "Есентуков",
                                           "lastName": "Артемьевич",
                                           "accountType": "EMPLOYEE",
                                           "birthDate": "1995-01-09"
                                         }
                                """)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Данные вашего аккаунт успешно обновлены."));

        var accountAfterSave = repository.findAccountById(savedAccount.getId());
        assertTrue(accountAfterSave.isPresent());
        assertEquals(accountAfterSave.get().getId(), savedAccount.getId());
        assertTrue(passwordEncoder.matches("TestPassword", accountAfterSave.get().getPassword()));
    }

    @Test
    @DisplayName("Test get info about user account.")
    void successfullyGetInfoAboutUserAccountTest() throws Exception {
        /* prepare data */
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(get(buildPath.apply("/data"))
                        .param("userId", savedAccount.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Данные аккаунта успешно получены"));
    }

    @Test
    @DisplayName("Test to get current account data.")
    void successfullyGetCurrentAccountDataTest() throws Exception{
        /* prepare data */
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(get(buildPath.apply("/current")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Данные аккаунта успешно получены"));
    }

    @Test
    @DisplayName("Test on delete existing account.")
    void successfullyDeleteExistingUserAccountTest() throws Exception {
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(delete(buildPath.apply("/delete")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ваш аккаунт был успешно удален."));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Test on create account with duplicated username.")
    void failureCreateAccountWithAlreadyExistingUsernameTest() throws Exception {
        /* prepare data */
        repository.save(buildTestAccount());

        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "Test",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(it -> assertEquals(
                        "Пользователь с данным username существует.",
                        extractErrorMessage(it))
                );
    }

    @Test
    @DisplayName("Test on change password with incorrect old password.")
    void failureChangePasswordTest() throws Exception {
        /* prepare data */
        var savedAccount = repository.save(buildTestAccount());
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(put(buildPath.apply("/change-password"))
                        .content("""
                                {
                                  "oldPassword": "TestPassword111",
                                  "newPassword": "new-password"
                                }
                                """)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(it -> assertEquals(
                        "Текущий пароль не совпадает с текущим, введенный пароль: TestPassword111",
                        extractErrorMessage(it))
                );
    }

    @Test
    @DisplayName("Test to create account without required data.")
    void failureCreateAccountWithoutUsernameTest() throws Exception{
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(it -> assertEquals(
                        "Невозможно создать пользователя, недостаточно данных.",
                        extractErrorMessage(it))
                );
    }

    @Test
    @DisplayName("Test to change account username on existing username in database.")
    void failureChangeAccountUsernameOnExistingTest() throws Exception{
        /* prepare data */
        var account1 = buildTestAccount();
        account1.setUsername("exist");
        var account2 = buildTestAccount();
        repository.save(account1);
        var savedAccount2 = repository.save(account2);
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount2.getId());

        mockMvc.perform(put(buildPath.apply("/edit"))
                        .content("""
                                        {
                                           "username": "exist",
                                           "email": "olegIv@mail.ru",
                                           "firstName": "Петр",
                                           "middleName": "Есентуков",
                                           "lastName": "Артемьевич",
                                           "accountType": "EMPLOYEE",
                                           "birthDate": "1995-01-09"
                                         }
                                """)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(it -> assertEquals(
                        "Пользователь с данным username существует.",
                        extractErrorMessage(it))
                );
    }

    /* convert request paths */
    private final Function<String, String> buildPath = postfix -> String.format("%s%s", ACCOUNT_PREFIX, postfix);

    /* extract error message for failure tests */
    private static String extractErrorMessage(MvcResult mvcResult){
        return mvcResult.getResponse().getErrorMessage();
    }
}
