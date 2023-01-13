/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager;

import a.gleb.clientmanager.configuration.ClientManagerConfiguration;
import a.gleb.clientmanager.security.WithJwt;
import a.gleb.clientmanager.service.OAuth2SecurityContextService;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.AccountRole;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@WithJwt
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK)
@Import(ClientManagerConfiguration.class)
public abstract class BaseClientManagerTest {

    public static final String ACCOUNT_PREFIX = "/api/v1/account";
    public static final String SUPPORT_PREFIX = "/api/v1/support";

    private static PostgreSQLContainer POSTGRES = new PostgreSQLContainer();

    @MockBean
    public OAuth2SecurityContextService oAuth2SecurityContextService;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public AccountRepository repository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    static {
        POSTGRES.withInitScript("init_script.sql");
        POSTGRES.start();
    }

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    /**
     * Method build test {@link Account}.
     *
     * @return account entity model with specific userId.
     */
    public Account buildTestAccount() {
        return Account.builder()
                .id(UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e"))
                .username("Test")
                .password(passwordEncoder.encode("TestPassword"))
                .firstName("Олег")
                .middleName("Иванов")
                .email("olegI3v@mail.ru")
                .birthData(LocalDate.now())
                .role(AccountRole.EMPLOYEE)
                .enabled(true)
                .build();
    }
}
