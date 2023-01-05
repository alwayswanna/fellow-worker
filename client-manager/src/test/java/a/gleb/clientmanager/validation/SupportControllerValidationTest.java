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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SupportControllerValidationTest extends BaseClientManagerTest {

    @Test
    @DisplayName("Find account by username which not exist.")
    void findAccountByUsernameWhichNotExistsTest() throws Exception {
        repository.save(buildTestAccount());

        mockMvc.perform(get(buildPath.apply("/account-by-username"))
                        .param("username", "TestOvii"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Disable not existing user account")
    void disableNotExistingAccountTest() throws Exception {
        mockMvc.perform(get(buildPath.apply("/disable-account"))
                        .param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    /* convert request paths */
    private final Function<String, String> buildPath = postfix -> String.format("%s%s", SUPPORT_PREFIX, postfix);
}
