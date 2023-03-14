/*
 * Copyright (c) 07-3/12/23, 5:21 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration;

import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@AllArgsConstructor
@EntityScan(basePackageClasses = {
        Account.class
})
@EnableJpaRepositories(basePackageClasses = {
        AccountRepository.class
})
@EnableConfigurationProperties(OAuth2ServerProperties.class)
public class OAuth2ServerConfiguration {

    private final OAuth2ServerProperties properties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(it -> {
            var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
            urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.getCors());
            it.configurationSource(urlBasedCorsConfigurationSource);
        });

        http
                .authorizeHttpRequests(authorize -> {
                            authorize.requestMatchers("/actuator/**").permitAll();
                            authorize.anyRequest().authenticated();
                        }

                )
                .formLogin(withDefaults());
        return http.build();
    }
}
