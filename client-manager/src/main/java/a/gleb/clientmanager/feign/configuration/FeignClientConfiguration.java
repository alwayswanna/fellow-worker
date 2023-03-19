/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.feign.configuration;

import a.gleb.clientmanager.configuration.properties.ClientManagerConfigurationProperties;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class FeignClientConfiguration {

    @Bean
    public Logger.Level logger(ClientManagerConfigurationProperties properties) {
        return properties.getFeignConfig().getLoggerLevel();
    }

    @Bean
    public Retryer retryer(ClientManagerConfigurationProperties properties) {
        var feignProperties = properties.getFeignConfig();
        return new Retryer.Default(
                feignProperties.getPeriod(),
                feignProperties.getMaxPeriod(),
                feignProperties.getAttempts()
        );
    }

    @Bean
    public RequestInterceptor requestInterceptor(
            ClientManagerConfigurationProperties properties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        return new OAuth2FeignRequestInterceptor(
                properties,
                clientRegistrationRepository,
                oAuth2AuthorizedClientService
        );
    }
}
