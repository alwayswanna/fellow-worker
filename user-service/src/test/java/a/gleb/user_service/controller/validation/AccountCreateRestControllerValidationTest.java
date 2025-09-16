package a.gleb.user_service.controller.validation;

import a.gleb.user_service.Oauth2ServerApplicationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Create user [validation].")
public class AccountCreateRestControllerValidationTest extends Oauth2ServerApplicationTests {

    @Test
    public void badRequestOnCreateWithEmptyUsername() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyPassword() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyFirstName() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyMiddleName() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyEmail() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestOnCreateWithEmptyDateOfBirth() throws Exception {
        mockMvc
                .perform(
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
                .andExpect(status().isBadRequest());
    }
}
