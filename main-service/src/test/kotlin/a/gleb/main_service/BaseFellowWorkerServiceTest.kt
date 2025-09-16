/*
 * Copyright (c) 07-1/8/23, 3:36 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.main_service

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension::class)
abstract class BaseFellowWorkerServiceTest {
}
