/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.security

import org.springframework.core.annotation.AliasFor
import org.springframework.security.test.context.support.WithSecurityContext
import java.lang.annotation.Inherited

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory::class)
annotation class WithJwt(
    @get:AliasFor("value") val name: String = "d0580c29-1fce-4900-820d-74765c46e28e",
    @get:AliasFor("name") val value: String = "d0580c29-1fce-4900-820d-74765c46e28e",
    val role: Array<String> = ["EMPLOYEE", "COMPANY"]
)
