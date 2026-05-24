package a.gleb.user_service.configuration;

import a.gleb.user_service.configuration.properties.UserServiceAppProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(UserServiceAppProperty.class)
public class UserServiceAppConfiguration {

    private final UserServiceAppProperty property;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(property.encryptionStrength());
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConf -> {
                    var basedCors = new UrlBasedCorsConfigurationSource();
                    basedCors.registerCorsConfiguration("/**", property.cors());
                    corsConf.configurationSource(basedCors);
                })
                .sessionManagement(it ->
                        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(it -> {
                    it.requestMatchers(property.unprotectedPatterns().toArray(String[]::new)).permitAll();
                    it.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults());

        return httpSecurity.build();
    }
}
