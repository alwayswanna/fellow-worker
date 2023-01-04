package a.gleb.clientmanager.controller;

import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.clientmanager.BaseClientManagerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class SupportControllerTest extends BaseClientManagerTest {

    @Test
    @DisplayName("Disable existing user account")
    void disableExistingAccountTest() throws Exception {
        var savedAccount = repository.save(buildTestAccount());

        mockMvc.perform(get(buildPath.apply("/disable-account"))
                        .param("userId", savedAccount.getId().toString()))
                .andExpect(status().isOk());

        var disabledAccount = repository.findAccountById(savedAccount.getId());
        assertTrue(disabledAccount.isPresent());
        assertFalse(disabledAccount.get().isEnabled());
    }

    @Test
    @DisplayName("Get all accounts which exists in database.")
    void getListOfExistingAccountsTest() throws Exception {
        repository.save(buildTestAccount());
        repository.save(buildTestAccount());
        repository.save(buildTestAccount());

        var jsonString = mockMvc.perform(get(buildPath.apply("/accounts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Список аккаунтов успешно получен"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var response = objectMapper.readValue(jsonString, ApiResponseModel.class);
        assertEquals(3, response.getAccounts().size());
    }

    @Test
    @DisplayName("Get account by username in database.")
    void findExistingAccountByUsernameTest() throws Exception {
        repository.save(buildTestAccount());

        mockMvc.perform(get(buildPath.apply("/account-by-username"))
                        .param("username", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Аккаунт по запросу"));
    }

    /* convert request paths */
    private final Function<String, String> buildPath = postfix -> String.format("%s%s", SUPPORT_PREFIX, postfix);
}
