package a.gleb.oauth2server.controller.validation;

import a.gleb.oauth2server.Oauth2ServerApplicationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Create role [validation].")
@WithMockUser(username = "user")
public class RoleCreateRestControllerValidationTest extends Oauth2ServerApplicationTests {

    @Test
    public void badRequestOnCreateRoleWithEmptyRoleName() throws Exception {
        mockMvc
                .perform(
                        post("/api/v1/role/create")
                                .content("""
                                        {
                                            "displayName": "System",
                                            "roleName": ""
                                        }
                                        """
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateRoleWithEmptyRoleDisplayName() throws Exception {
        mockMvc
                .perform(
                        post("/api/v1/role/create")
                                .content("""
                                        {
                                            "displayName": "",
                                            "roleName": "SYSTEM"
                                        }
                                        """
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
