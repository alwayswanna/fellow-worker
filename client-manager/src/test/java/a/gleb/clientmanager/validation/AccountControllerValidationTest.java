/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.validation;

import a.gleb.clientmanager.BaseClientManagerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test instance which checks validation in rest-api.
 */
class AccountControllerValidationTest extends BaseClientManagerTest {

    @Test
    @DisplayName("Test with empty password.")
    void createAccountInvalidEmptyPasswordTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with empty username.")
    void createAccountInvalidEmptyUsernameTest() throws Exception {
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with empty first name.")
    void createAccountInvalidEmptyFirstNameTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with empty middle name.")
    void createAccountInvalidEmptyMiddleNameTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with empty birth date.")
    void createAccountInvalidEmptyBirthDateTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Oleg",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": null
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with empty email.")
    void createAccountInvalidEmptyEmailTest() throws Exception {
        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "username",
                                  "password": "password",
                                  "email": "",
                                  "firstName": "Oleg",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with user credentials (username) which already in use.")
    void createAccountUsernameWhichAlreadyExistTest() throws Exception {
        /* prepare data */
        var account = buildTestAccount();
        account.setUsername("ExistingUsername");
        repository.save(account);

        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "ExistingUsername",
                                  "password": "TestPassword",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test with user credentials (email) which already in use.")
    void createAccountEmailWhichAlreadyExistTest() throws Exception {
        /* prepare data */
        var account = buildTestAccount();
        account.setEmail("existing-email@yahoo.com");
        repository.save(account);

        mockMvc.perform(post(buildPath.apply("/create"))
                        .content("""
                                {
                                  "username": "Tes3tUsername3",
                                  "password": "TestPassword",
                                  "email": "existing-email@yahoo.com",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test change password action with empty old password.")
    void changeAccountOldPasswordEmptyTest() throws Exception {
        mockMvc.perform(put(buildPath.apply("/change-password"))
                        .content("""
                                {
                                   "oldPassword": "",
                                   "newPassword": "new-password"
                                 }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test change password action with empty new password.")
    void changeAccountNewPasswordEmptyTest() throws Exception {
        mockMvc.perform(put(buildPath.apply("/change-password"))
                        .content("""
                                {
                                   "oldPassword": "old-password",
                                   "newPassword": ""
                                 }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test change password action with invalid old password.")
    void changeAccountOldPasswordInvalidTest() throws Exception {
        var account = buildTestAccount();
        var savedAccount = repository.save(account);
        when(oAuth2SecurityContextService.getUserId()).thenReturn(savedAccount.getId());

        mockMvc.perform(put(buildPath.apply("/change-password"))
                        .content("""
                                {
                                   "oldPassword": "TestPassword_Password",
                                   "newPassword": "new-password"
                                 }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        var afterTestAccount = repository.findAccountById(savedAccount.getId());
        assertTrue(afterTestAccount.isPresent());
        assertEquals(afterTestAccount.get().getPassword(), savedAccount.getPassword());
    }

    @Test
    @DisplayName("Test on edit with user credentials (username) which already in use.")
    void changeAccountUsernameAlreadyInUseTest() throws Exception {
        /* prepare data */
        var account = buildTestAccount();
        account.setUsername("existing-username");
        repository.save(account);

        mockMvc.perform(put(buildPath.apply("/edit"))
                        .content("""
                                {
                                  "username": "existing-username",
                                  "password": "TestPassword",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test on edit with user credentials (email) which already in use.")
    void changeAccountEmailAlreadyInUseTest() throws Exception {
        /* prepare data */
        var account = buildTestAccount();
        account.setEmail("existing-email@gmail.com");
        repository.save(account);

        mockMvc.perform(put(buildPath.apply("/edit"))
                        .content("""
                                {
                                  "username": "TestUsername1",
                                  "password": "TestPassword",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test on edit when account does not exist.")
    void changeAccountUserWithCurrentIdDoesNotExistsTest() throws Exception {
        /* prepare data */
        var account = buildTestAccount();
        account.setId(UUID.randomUUID());
        repository.save(account);

        mockMvc.perform(put(buildPath.apply("/edit"))
                        .content("""
                                {
                                  "username": "TestUsername1",
                                  "password": "TestPassword",
                                  "email": "olegIv@mail.ru",
                                  "firstName": "Олег",
                                  "middleName": "Иванов",
                                  "lastName": "Игоревич",
                                  "accountType": "COMPANY",
                                  "birthDate": "2023-01-04"
                                }
                                """)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test get info about user account when account does not exists.")
    void getInfoAboutUserAccountTest() throws Exception {
        mockMvc.perform(get(buildPath.apply("/data"))
                        .param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    /* convert request paths */
    private final Function<String, String> buildPath = postfix -> String.format("%s%s", ACCOUNT_PREFIX, postfix);
}
