package a.gleb.oauth2server.controller.validation;

import a.gleb.oauth2server.Oauth2ServerApplicationTests;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(username = "user")
public class RoleRestControllerValidationTest extends Oauth2ServerApplicationTests {
}
