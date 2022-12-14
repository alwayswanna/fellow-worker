/*
 * Copyright (c) 12/28/22, 7:57 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.clientmanager.configuration.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("client-manager")
public class ClientManagerConfigurationProperties {

    @NotNull
    private List<SecurityConstraints> securityConstraints;
    @NotNull
    private List<String> unprotectedPatterns;

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
}

