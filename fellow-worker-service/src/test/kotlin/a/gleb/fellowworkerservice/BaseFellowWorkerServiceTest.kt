/*
 * Copyright (c) 07-1/8/23, 3:36 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice

import a.gleb.fellowworkerservice.db.repository.ResumeRepository
import a.gleb.fellowworkerservice.db.repository.VacancyRepository
import a.gleb.fellowworkerservice.security.WithJwt
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@WithJwt
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension::class)
abstract class BaseFellowWorkerServiceTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var resumeRepository: ResumeRepository

    @Autowired
    protected lateinit var vacancyRepository: VacancyRepository

    companion object {

        private val RABBITMQ_CONTAINER = RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"))
        private val MONGO_DB_CONTAINER = MongoDBContainer(DockerImageName.parse("mongo:latest"))

        init {
            RABBITMQ_CONTAINER.start()
            MONGO_DB_CONTAINER.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { MONGO_DB_CONTAINER.connectionString }
            registry.add("spring.rabbitmq.host") { RABBITMQ_CONTAINER.host }
            registry.add("spring.rabbitmq.port") { RABBITMQ_CONTAINER.firstMappedPort }
        }
    }
}
