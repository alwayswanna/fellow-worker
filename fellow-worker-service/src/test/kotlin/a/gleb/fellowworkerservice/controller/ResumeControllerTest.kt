/*
 * Copyright (c) 07-1/10/23, 11:15 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.controller

import a.gleb.fellowworkerservice.BaseFellowWorkerServiceTest
import a.gleb.fellowworkerservice.db.dao.ContactModel
import a.gleb.fellowworkerservice.db.dao.Education
import a.gleb.fellowworkerservice.db.dao.Resume
import a.gleb.fellowworkerservice.db.dao.WorkExperience
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

const val EMPLOYEE_PATH = "/api/v1/employee"

class ResumeControllerTest : BaseFellowWorkerServiceTest() {

    @AfterEach
    fun tearDown() {
        GlobalScope.launch(Unconfined) {
            resumeRepository.deleteAll()
        }
    }

    @Test
    fun `successfully create resume`() {
        val request = """
            {
              "firstName": "Аркадий",
              "middleName": "Нахимов",
              "lastName": "Олегович",
              "birthDate": "1982-11-02",
              "job": "Начальник склада",
              "expectedSalary": "40000",
              "about": "Увлекаюсь ... Хобби",
              "education": [
                {
                  "startTime": "2016-09-01",
                  "endTime": "2021-06-01",
                  "educationalInstitution": "Башкирский государственный университет",
                  "educationLevel": "MAGISTRACY"
                }
              ],
              "professionalSkills": [
                "string"
              ],
              "workingHistory": [
                {
                  "startTime": "2021-09-01",
                  "endTime": "2022-06-01",
                  "companyName": "string",
                  "workingSpecialty": "Заведующий хоз. учета",
                  "responsibilities": [
                    "string"
                  ],
                  "tags": [
                    "string"
                  ]
                }
              ],
              "contact": {
                    "phone": "+79008005544",
                    "email": "worker_mail@yandex.ru"
              },
              "base64Image": "string"
            }
        """.trimIndent()

        webTestClient.post()
            .uri(EMPLOYEE_PATH.plus("/create-resume"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Ваше резюме успешно опубликовано. PDF версия станет доступна чуть позже.")


        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully create resume with minimum fields`() {
        val request = """
            {
              "firstName": "Аркадий",
              "middleName": "Нахимов",
              "birthDate": "1982-11-02",
              "job": "Начальник склада",
              "professionalSkills": [
                "string"
              ],
              "contact": {
                    "phone": "+79008005544",
                    "email": "worker_mail@yandex.ru"
              }
            }
        """.trimIndent()

        webTestClient.post()
            .uri(EMPLOYEE_PATH.plus("/create-resume"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Ваше резюме успешно опубликовано. PDF версия станет доступна чуть позже.")


        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get all resume`() {
        /* prepare data */
        saveResume(createDefaultResume())
        TimeUnit.SECONDS.sleep(2)

        webTestClient.get()
            .uri(EMPLOYEE_PATH.plus("/get-all-resume"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get resume for current user`() {
        /* prepare data */
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0280c29-1fce-4900-820d-74762c46e28e")
        saveResume(resume)
        TimeUnit.SECONDS.sleep(2)

        webTestClient.get()
            .uri(EMPLOYEE_PATH.plus("/current-user-resume"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get resume by id`() {
        /* prepare data */
        val resume = createDefaultResume()
        saveResume(resume)
        TimeUnit.SECONDS.sleep(2)

        webTestClient.get()
            .uri {
                it
                    .path(EMPLOYEE_PATH.plus("/get-resume-id"))
                    .queryParam("id", resume.id.toString())
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully delete resume by id`() {
        /* prepare data */
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveResume(resume)
        TimeUnit.SECONDS.sleep(2)

        webTestClient.delete()
            .uri {
                it
                    .path(EMPLOYEE_PATH.plus("/delete-resume"))
                    .queryParam("id", resume.id.toString())
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(0, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully edit user resume`() {
        /* prepare data */
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveResume(resume)
        TimeUnit.SECONDS.sleep(2)

        val request = """
            {
              "resumeId": "${resume.id}",
              "firstName": "Аркадий",
              "middleName": "Нахимов",
              "lastName": "Олегович",
              "birthDate": "1982-11-02",
              "job": "Начальник склада",
              "expectedSalary": "40000",
              "about": "Увлекаюсь ... Хобби",
              "education": [
                {
                  "startTime": "2016-09-01",
                  "endTime": "2021-06-01",
                  "educationalInstitution": "Башкирский государственный университет",
                  "educationLevel": "MAGISTRACY"
                }
              ],
              "professionalSkills": [
                "string"
              ],
              "workingHistory": [
                {
                  "startTime": "2021-09-01",
                  "endTime": "2022-06-01",
                  "companyName": "string",
                  "workingSpecialty": "Заведующий хоз. учета",
                  "responsibilities": [
                    "string"
                  ],
                  "tags": [
                    "string"
                  ]
                }
              ],
              "contact": {
                    "phone": "+79008005544",
                    "email": "worker_mail@yandex.ru"
              },
              "base64Image": "string"
            }
        """.trimIndent()

        webTestClient.put()
            .uri(EMPLOYEE_PATH.plus("/edit-resume"))
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Ваше резюме успешно обновлено. PDF версия станет доступна чуть позже.")

        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    /* save resume for test */
    private fun saveResume(resume: Resume) {
        GlobalScope.launch(Unconfined) {
            resumeRepository.save(resume)
        }
    }

    /* create default entity for test */
    fun createDefaultResume(): Resume {
        return Resume(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Oleg",
            "Petrov",
            null,
            LocalDate.now(),
            "Worker",
            "2000$",
            "About me",
            listOf(
                Education(
                    LocalDate.now().minusYears(2),
                    LocalDate.now(),
                    "BashGU",
                    ""
                )
            ),
            listOf("Clever", "Soft", "etc"),
            listOf(
                WorkExperience(
                    LocalDate.now().minusYears(2),
                    LocalDate.now(),
                    "Company",
                    "Worker",
                    listOf("list", "list"),
                    listOf("tags", "tags")
                )
            ),
            ContactModel("89008008888", "test_resume@yandex.ru"),
            LocalDateTime.now()
        )
    }
}