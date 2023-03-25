/*
 * Copyright (c) 12-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.configuration.properties;

import feign.Logger;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static feign.Logger.Level.NONE;

@Getter
@Setter
@ConfigurationProperties("client-manager")
public class ClientManagerConfigurationProperties {

    /**
     * Methods which close with JWT `roles`
     */
    @NotNull
    private List<SecurityConstraints> securityConstraints;

    /**
     * Methods without security
     */
    @NotNull
    private List<String> unprotectedPatterns;

    @NotNull
    private Cors cors;

    /**
     * Configuration for FeignClient-s
     */
    @NotNull
    private FeignClientConfiguration feignConfig;

    @Getter
    @Setter
    public static class SecurityConstraints {

        private List<SecurityCollection> securityCollections;
        private List<String> roles;
    }

    @Getter
    @Setter
    public static class SecurityCollection {

        private List<String> patterns;
        private List<String> methods;
    }

    @Getter
    @Setter
    public static class Cors extends CorsConfiguration {

        /* отвечает за конфигурацию CORS, по умолчанию false */
        private boolean enabled = false;
    }

    @Getter
    @Setter
    public static class FeignClientConfiguration {

        private Logger.Level loggerLevel = NONE;

        private String registrationId;

        private long period;

        private long maxPeriod;

        private int attempts = 3;
    }
}

