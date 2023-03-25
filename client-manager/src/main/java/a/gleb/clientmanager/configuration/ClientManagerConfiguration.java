/*
 * Copyright (c) 07-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.configuration;

import a.gleb.clientmanager.configuration.properties.ClientManagerConfigurationProperties;
import a.gleb.clientmanager.feign.ResumeVacancyFeignClient;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.DeletedAccount;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import a.gleb.oauth2persistence.db.repository.DeletedAccountRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableJpaRepositories(basePackageClasses = {
        AccountRepository.class,
        DeletedAccountRepository.class
})
@EntityScan(basePackageClasses = {
        Account.class,
        DeletedAccount.class
})
@EnableFeignClients(clients = {
        ResumeVacancyFeignClient.class
})
@EnableConfigurationProperties(ClientManagerConfigurationProperties.class)
public class ClientManagerConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
