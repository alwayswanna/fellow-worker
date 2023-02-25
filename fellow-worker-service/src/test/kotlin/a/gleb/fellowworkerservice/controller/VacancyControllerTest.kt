/*
 * Copyright (c) 07-3/1/23, 10:18 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.controller

import a.gleb.fellowworkerservice.BaseFellowWorkerServiceTest
import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.TypePlacement
import a.gleb.fellowworkerservice.db.dao.Vacancy
import a.gleb.fellowworkerservice.db.dao.VacancyContactInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

const val VACANCY_PATH = "/api/v1/vacancy"

class VacancyControllerTest : BaseFellowWorkerServiceTest() {

    @AfterEach
    fun tearDown() {
        GlobalScope.launch(Dispatchers.Unconfined) {
            vacancyRepository.deleteAll()
        }
    }

    @Test
    fun `successfully create vacancy`() {
        val request = """
            {
              "vacancyName": "Сварщик",
              "typeOfWork": "FULL_EMPLOYMENT",
              "typeOfWorkPlacement": "OFFICE",
              "companyName": "ООО СварТехКомплект",
              "companyFullAddress": "г. Москва Южное Бутово, Проспект молодежи 3/1",
              "keySkills": [
                "string"
              ],
              "cityName": "Москва",
              "workingResponsibilities": [
                "string"
              ],
              "companyBonuses": [
                "string"
              ],
              "contactApiModel": {
                "fio": "Петров Игорь Васильевич",
                "phone": "+79998087788",
                "email": "petrov@ya.ru"
              }
            }
        """.trimIndent()

        webTestClient.post()
            .uri(VACANCY_PATH.plus("/create"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Ваша вакансия успешно создана.")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully edit vacancy`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        vacancy.ownerId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        val request = """
            {
              "vacancyId": "${vacancy.id}",
              "vacancyName": "Сварщик",
              "typeOfWork": "FULL_EMPLOYMENT",
              "typeOfWorkPlacement": "OFFICE",
              "companyName": "ООО СварТехКомплект",
              "companyFullAddress": "г. Москва Южное Бутово, Проспект молодежи 3/1",
              "keySkills": [
                "string"
              ],
              "cityName": "Москва",
              "workingResponsibilities": [
                "string"
              ],
              "companyBonuses": [
                "string"
              ],
              "contactApiModel": {
                "fio": "Петров Игорь Васильевич",
                "phone": "+79998087788",
                "email": "petrov@ya.ru"
              }
            }
        """.trimIndent()

        webTestClient.put()
            .uri(VACANCY_PATH.plus("/edit"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Ваша вакансия успешно отредактирована.")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get all vacancies`() {
        /* prepare data */
        saveVacancy(createDefaultVacancy())
        saveVacancy(createDefaultVacancy())
        saveVacancy(createDefaultVacancy())
        TimeUnit.SECONDS.sleep(3)

        webTestClient.get()
            .uri(VACANCY_PATH.plus("/vacancy-all"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(3, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get vacancy by id`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        webTestClient.get()
            .uri {
                it
                    .path(VACANCY_PATH.plus("/id"))
                    .queryParam("vacancyId", vacancy.id.toString())
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully delete vacancy by id`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        vacancy.ownerId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        webTestClient.delete()
            .uri {
                it
                    .path(VACANCY_PATH.plus("/delete-id"))
                    .queryParam("vacancyId", vacancy.id.toString())
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(0, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully find vacancies by type placement`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        val request = """
            {
                "city": null,
                "typeOfWork": null,
                "typeOfWorkPlacement": "${vacancy.typeOfWorkPlacement}",
                "keySkills": null
            }
        """.trimIndent()

        webTestClient.post()
            .uri(VACANCY_PATH.plus("/filter-vacancies"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Вот что мы нашли")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully find vacancies by type time`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        val request = """
            {
                "city": null,
                "typeOfWork": "${vacancy.typeWork}",
                "typeOfWorkPlacement": null,
                "keySkills": null
            }
        """.trimIndent()

        webTestClient.post()
            .uri(VACANCY_PATH.plus("/filter-vacancies"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Вот что мы нашли")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully find vacancies by city`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        val request = """
            {
                "city": "${vacancy.cityName}",
                "typeOfWork": null,
                "typeOfWorkPlacement": null,
                "keySkills": null
            }
        """.trimIndent()

        webTestClient.post()
            .uri(VACANCY_PATH.plus("/filter-vacancies"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Вот что мы нашли")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully find vacancies by key skill`() {
        /* prepare data */
        val vacancy = createDefaultVacancy()
        saveVacancy(vacancy)
        TimeUnit.SECONDS.sleep(3)

        val request = """
            {
                "city": null,
                "typeOfWork": null,
                "typeOfWorkPlacement": null,
                "keySkills": "${vacancy.keySkills[0]}"
            }
        """.trimIndent()

        webTestClient.post()
            .uri(VACANCY_PATH.plus("/filter-vacancies"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Вот что мы нашли")

        assertEquals(1, vacancyRepository.findAll().asFlux().toStream().toList().size)
    }

    /* save vacancy for test */
    private fun saveVacancy(vacancy: Vacancy) {
        GlobalScope.launch(Dispatchers.Unconfined) {
            vacancyRepository.save(vacancy)
        }
    }

    /* create default entity for test */
    fun createDefaultVacancy(): Vacancy {
        return Vacancy(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "NameVacancy",
            "15000",
            TypeOfWork.FULL_EMPLOYMENT,
            TypePlacement.OFFICE,
            "CompanyName",
            "Moscow, pr",
            listOf("key1", "key2", "key2"),
            "Moscow",
            listOf("resp1", "resp2", "resp3"),
            listOf("bonus1", "bonus2", "bonus3"),
            VacancyContactInfo(
                "Alena Pavlovna",
                "+79993003030",
                "test@mail.ru"
            ),
            LocalDateTime.now()
        )
    }
}