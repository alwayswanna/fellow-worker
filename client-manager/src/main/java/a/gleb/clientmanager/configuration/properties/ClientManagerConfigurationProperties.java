package a.gleb.clientmanager.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("client-manager")
public class ClientManagerConfigurationProperties {

    private List<SecurityConstraints> securityConstraints;

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

