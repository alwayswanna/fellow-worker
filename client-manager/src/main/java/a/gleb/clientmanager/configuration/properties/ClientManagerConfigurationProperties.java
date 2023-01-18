/*
 * Copyright (c) 12-1/18/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.configuration.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("client-manager")
public class ClientManagerConfigurationProperties {

    @NotNull
    private List<SecurityConstraints> securityConstraints;
    @NotNull
    private List<String> unprotectedPatterns;
    @NotNull
    private Cors cors;

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
}

