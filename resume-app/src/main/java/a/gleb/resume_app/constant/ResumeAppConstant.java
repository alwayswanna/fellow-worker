package a.gleb.resume_app.constant;

import java.util.Set;

public class ResumeAppConstant {
    public static final String REQUESTOR = "requestor";
    public static final String TRACE_ID_RESPONSE_HEADER = "X-Trace-ID";
    public static final String OAUTH_SECURITY_SCHEME = "oAuth2Scheme";
    public static final String ADMIN_ROLE_CODE = "ADMIN";
    public static final Set<String> ALLOWED_PHOTO_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    public static final Set<String> ALLOWED_PHOTO_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private ResumeAppConstant() {}
}
