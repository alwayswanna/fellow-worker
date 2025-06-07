package a.gleb.oauth2server.constant;

import a.gleb.oauth2server.scheduler.RemoveExpiredOauth2AuthorizationScheduledTask;

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

    private OAuth2ServerConstants() {
    }
}
