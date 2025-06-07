/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.service.authorization;

import a.gleb.oauth2server.db.entity.AccountEntity;
import a.gleb.oauth2server.exception.UsernameNotFoundException;
import a.gleb.oauth2server.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

import static a.gleb.oauth2server.exception.UsernameNotFoundException.nullableUsername;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2UserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            nullableUsername();
        }

        return accountService.findAccountByUsernameOrEmail(username)
                .map(account ->
                        User.builder()
                                .username(account.getUsername())
                                .password(account.getPassword())
                                .disabled(!account.isEnabled())
                                .authorities(getAccountAuthorities(account))
                                .build()
                ).orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("User with username %s does`t exists.", username)
                        )
                );
    }

    private static List<SimpleGrantedAuthority> getAccountAuthorities(AccountEntity account) {
        return account.getRoles()
                .stream()
                .map(it -> new SimpleGrantedAuthority(it.getRoleName()))
                .toList();
    }
}
