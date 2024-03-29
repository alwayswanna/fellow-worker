/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithJwt> {

    @Override
    public SecurityContext createSecurityContext(WithJwt user) {
        Jwt jwt = Jwt.withTokenValue("must-be-unused")
                .header("must-be-unused", "must-be-unused")
                .claim("preferred_username", user.value())
                .claim("user_id", user.name())
                .build();
        List<SimpleGrantedAuthority> grantedAuthorities =
                (List) Arrays.stream(user.role())
                        .map((role) -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                        .collect(Collectors.toList());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new JwtAuthenticationToken(jwt, grantedAuthorities));
        return ctx;
    }
}
