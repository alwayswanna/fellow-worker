package a.gleb.user_app.constant;

public final class UserAppConstant {

    public static final String LOCK_TABLE = "user.shedlock";

    public static final String AUTHORIZATION_PARTITION_PATTERN = "authorization_%s_%s";

    public static final String REQUESTOR = "requestor";

    public static final String TRACE_ID_RESPONSE_HEADER = "X-Trace-ID";

    //language=SQL
    public static final String CREATE_PARTITION_QUERY = """
                CREATE TABLE IF NOT EXISTS %s PARTITION OF "authorization" FOR VALUES FROM ('%s') TO ('%s')
            """;

    //language=SQL
    public static final String DROP_PARTITION_QUERY = """
                DROP TABLE IF EXISTS %s
            """;

    private UserAppConstant() {}
}
