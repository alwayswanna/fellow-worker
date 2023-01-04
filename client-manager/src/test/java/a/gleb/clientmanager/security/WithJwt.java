package a.gleb.clientmanager.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(
        factory = TestSecurityContextFactory.class
)
public @interface WithJwt {

    @AliasFor("value")
    String name() default "d0580c29-1fce-4900-820d-74765c46e28e";

    @AliasFor("name")
    String value() default "d0580c29-1fce-4900-820d-74765c46e28e";

    String[] role() default {"EMPLOYEE", "ADMIN"};
}
