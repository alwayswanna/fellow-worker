package a.gleb.oauth2server.constant;

import a.gleb.oauth2server.scheduler.RemoveExpiredOauth2AuthorizationScheduledTask;

public final class OAuth2ServerConstants {

    /**
     * Query for remove expired user`s session for database.
     * Used: {@link RemoveExpiredOauth2AuthorizationScheduledTask#removeExpiredSessions()}
     */
    public static final String SQL_REMOVE_EXPIRED_SESSIONS =
            """
                        delete from oauth2_authorization where refresh_token_expires_at < (?);
                    """;

    private OAuth2ServerConstants() {
    }
}
