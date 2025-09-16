package a.gleb.user_service.constant;

import a.gleb.user_service.scheduler.RemoveExpiredOauth2AuthorizationScheduledTask;

import java.time.format.DateTimeFormatter;

public final class OAuth2ServerConstants {

    /**
     * Query for remove expired user`s session for database.
     * Used: {@link RemoveExpiredOauth2AuthorizationScheduledTask#removeExpiredSessions()}
     */
    public static final String SQL_REMOVE_EXPIRED_SESSIONS = """
                delete from oauth2_authorization where refresh_token_expires_at < (?);
            """;

    /**
     * Delimiter char for store values in database.
     */
    public static final String COMMA = ",";
    /**
     * Custom claim for token.
     */
    public static final String ROLES_CLAIM = "roles";
    /**
     * Max entities on page, limit param for database queries.
     */
    public static final int MAX_ENTITIES_PER_PAGE = 25;
    /**
     * Security scheme name, Swagger-configuration.
     */
    public static final String OAUTH2_SERVER_DEFINITION = "oAuth2ServerSecurityScheme";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    public static final String ERROR_MESSAGE_KEY = "message";
    public static final String ERROR_TIMESTAMP_KEY = "timestamp";
    public static final String ERROR_PATH_KEY = "path";
    public static final String DEFAULT_ROLE_CODE_ON_ACCOUNT_CREATED = "UNRECOGNIZED";

    private OAuth2ServerConstants() {
    }
}
