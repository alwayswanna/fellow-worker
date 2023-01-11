/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service

const val USER_ID_CLAIM = "user_id"

@Service
class Oauth2SecurityService {

    suspend fun extractOauth2UserId(): String {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication as JwtAuthenticationToken }
            .map { it: JwtAuthenticationToken ->
                val mutableMap = it.tokenAttributes
                mutableMap[USER_ID_CLAIM].toString()
            }
            .awaitSingle();
    }
}