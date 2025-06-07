package a.gleb.oauth2server.controller.validation;

import a.gleb.oauth2server.Oauth2ServerApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WithMockUser(username = "user")
public class AccountRestControllerValidationTest extends Oauth2ServerApplicationTests {

    @Test
    public void badRequestOnCreateWithEmptyUsername() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "",
                                           "password": "somePassword",
                                           "firstName": "Test",
                                           "lastName": "TestLastName",
                                           "middleName": "MiddleTestName",
                                           "email": "test@test.com",
                                           "enabled": true,
                                           "birthDate": "2000-01-01"
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyPassword() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "someUsername",
                                           "password": "",
                                           "firstName": "Test",
                                           "lastName": "TestLastName",
                                           "middleName": "MiddleTestName",
                                           "email": "test@test.com",
                                           "enabled": true,
                                           "birthDate": "2000-01-01"
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyFirstName() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "someUsername",
                                           "password": "somePassword",
                                           "firstName": "",
                                           "lastName": "TestLastName",
                                           "middleName": "MiddleTestName",
                                           "email": "test@test.com",
                                           "enabled": true,
                                           "birthDate": "2000-01-01"
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyMiddleName() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "someUsername",
                                           "password": "somePassword",
                                           "firstName": "FirstName",
                                           "lastName": "LastName",
                                           "middleName": "",
                                           "email": "test@test.com",
                                           "enabled": true,
                                           "birthDate": "2000-01-01"
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyEmail() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "someUsername",
                                           "password": "somePassword",
                                           "firstName": "FirstName",
                                           "lastName": "LastName",
                                           "middleName": "MiddleName",
                                           "email": "",
                                           "enabled": true,
                                           "birthDate": "2000-01-01"
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyDateOfBirth() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create")
                                .content("""
                                        {
                                           "username": "someUsername",
                                           "password": "somePassword",
                                           "firstName": "FirstName",
                                           "lastName": "LastName",
                                           "middleName": "MiddleName",
                                           "email": "test@test.com",
                                           "enabled": true,
                                           "birthDate": ""
                                         }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
