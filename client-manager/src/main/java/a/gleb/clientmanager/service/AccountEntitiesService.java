/*
 * Copyright (c) 3-3/30/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.clientmanager.feign.ResumeVacancyFeignClient;
import a.gleb.clientmanager.service.db.DeletedAccountDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AccountEntitiesService {

    private final DeletedAccountDatabaseService deletedAccountDatabaseService;
    private final ResumeVacancyFeignClient resumeVacancyFeignClient;

    /**
     * Method send request to remove user entities in foreign services.
     */
    public void removeUserEntities(UUID userId) {
        log.info("Start remove user entities. [id: {}]", userId);
        boolean result = false;
        try {
            resumeVacancyFeignClient.removeUserEntities(userId);
            result = true;
        } catch (Exception e) {
            deletedAccountDatabaseService.safe(userId);
            log.error("Error while remove user entities. [id: {}]", userId, e);
        }
        log.info("End attempts to remove. Result delete: {}, [id: {}]", result, userId);
    }

    /**
     * Method send request to remove user entities in foreign services for scheduled task.
     */
    public void remove(UUID userId) {
        log.info("Start remove user entities. [id: {}]", userId);
        resumeVacancyFeignClient.removeUserEntities(userId);
        log.info("End attempts to remove. [id: {}]", userId);
    }
}
