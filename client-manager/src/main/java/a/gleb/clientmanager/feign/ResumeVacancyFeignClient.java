/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.feign;

import a.gleb.clientmanager.feign.configuration.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "resume-vacancy-client",
        url = "${client-manager.feign-config.resume-vacancy-client.url}",
        configuration = FeignClientConfiguration.class
)
public interface ResumeVacancyFeignClient {

    @GetMapping("/api/v1/support/remove")
    void removeUserEntities(@RequestParam UUID id);
}
