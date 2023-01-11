/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContextFactory


class TestSecurityContextFactory : WithSecurityContextFactory<WithJwt> {

    override fun createSecurityContext(user: WithJwt): SecurityContext {
        val jwt = Jwt.withTokenValue("must-be-unused")
            .header("must-be-unused", "must-be-unused")
            .claim("preferred_username", user.value)
            .claim("user_id", user.name)
            .build()

        val grantedAuthority: List<SimpleGrantedAuthority> = user.role.asSequence()
            .map { SimpleGrantedAuthority("ROLE_$it") }
            .toList()
        val ctx = SecurityContextHolder.createEmptyContext()
        ctx.authentication = JwtAuthenticationToken(jwt, grantedAuthority)
        return ctx
    }
}