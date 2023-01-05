package a.gleb.fellowworkerservice.controller

import a.gleb.fellowworkerservice.BaseFellowWorkerServiceTest
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
              "birthDate": "1985-11-03",
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
              "birthDate": "1985-11-03",
              "job": "Начальник склада",
              "professionalSkills": [
                "string"
              ]
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
        saveResume(createDefaultResume())
        saveResume(createDefaultResume())

        webTestClient.get()
            .uri(EMPLOYEE_PATH.plus("/get-all-resume"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(2, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get resume for current user`() {
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveResume(resume)

        webTestClient.get()
            .uri(EMPLOYEE_PATH.plus("/current-user-resume"))
            .exchange()
            .expectStatus()
            .isOk

        assertEquals(1, resumeRepository.findAll().asFlux().toStream().toList().size)
    }

    @Test
    fun `successfully get resume by id`() {
        val resume = createDefaultResume()
        saveResume(resume)

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
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveResume(resume)
        TimeUnit.SECONDS.sleep(5);

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
        val resume = createDefaultResume()
        resume.ownerRecordId = UUID.fromString("d0580c29-1fce-4900-820d-74765c46e28e")
        saveResume(resume)

        val request = """
            {
              "resumeId": "${resume.id}",
              "firstName": "Аркадий",
              "middleName": "Нахимов",
              "lastName": "Олегович",
              "birthDate": "1985-11-03",
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
            "5000$",
            "About me",
            listOf(
                Education(
                    LocalDate.now().minusYears(3),
                    LocalDate.now(),
                    "BashGU",
                    ""
                )
            ),
            listOf("Clever", "Soft", "etc"),
            listOf(
                WorkExperience(
                    LocalDate.now().minusYears(3),
                    LocalDate.now(),
                    "Company",
                    "Worker",
                    listOf("list", "list"),
                    listOf("tags", "tags")
                )
            ),
            LocalDateTime.now()
        )
    }
}