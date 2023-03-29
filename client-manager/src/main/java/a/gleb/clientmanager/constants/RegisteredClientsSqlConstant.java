/*
 * Copyright (c) 3-3/29/23, 8:31 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.constants;

public final class RegisteredClientsSqlConstant {

    public static final String SQL_SECRET_UPDATE_QUERY =
            """
                UPDATE oauth2_registered_client SET client_secret = (?) WHERE client_id = (?);
            """;

    public static final String SQL_SELECT_REDIRECT_URIS_QUERY =
            """
               SELECT redirect_uris FROM oauth2_registered_client WHERE client_id = (?);
            """;

    public static final String SQL_UPDATE_REDIRECT_URIS_QUERY =
            """
                UPDATE oauth2_registered_client SET redirect_uris = (?) WHERE client_id = (?);
            """;

    public static final String SQL_REDIRECT_URI_COLUMN_NAME = "redirect_uris";

    private RegisteredClientsSqlConstant() {}
}
