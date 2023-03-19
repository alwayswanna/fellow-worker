/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageDelete
import a.gleb.fellowworkerservice.db.repository.ResumeRepository
import a.gleb.fellowworkerservice.db.repository.VacancyRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger { }

@Service
class SupportService(
    private val resumeRepository: ResumeRepository,
    private val vacancyRepository: VacancyRepository,
    private val resumeSenderService: ResumeSenderService
) {

    /**
     * Method removes user vacancies or repositories by owner ID
     * @param id userid from request
     */
    suspend fun removeUserEntities(id: UUID) {
        logger.info { "Start remove user entities. [ID: $id]" }
        val userResumes = resumeRepository.findByOwnerRecordId(id)

        if (userResumes != null) {
            resumeRepository.delete(userResumes)
            resumeSenderService.sendMessageRemove(ResumeMessageDelete(userResumes.id))
        } else {
            val userVacancies = vacancyRepository.findAllByOwnerId(id)
            logger.info { "User vacancies count is: ${userVacancies.count()}" }
            if (userVacancies.isNotEmpty()) {
                vacancyRepository.deleteAll(userVacancies)
            }
        }

        logger.info { "End remove user entities. [ID: $id]" }
    }
}