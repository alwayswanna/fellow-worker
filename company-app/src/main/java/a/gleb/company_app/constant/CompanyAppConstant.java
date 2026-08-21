package a.gleb.company_app.constant;

public class CompanyAppConstant {

    public static final String OAUTH_SECURITY_SCHEME = "oAuth2Scheme";
    public static final String ERROR_MESSAGE_TEMPLATE = "ID: %s - %s: %s, [trace_id=%s]";
    public static final String TRACE_ID_RESPONSE_HEADER = "X-Trace-ID";
    public static final String REQUESTOR = "requestor";
    public static final String LOCK_TABLE = "company.shedlock";

    private CompanyAppConstant() {}
}
