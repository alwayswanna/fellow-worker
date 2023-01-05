/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.service;

import a.gleb.oauth2server.exception.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2UserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) throw new UsernameNotFoundException(
                BAD_REQUEST,
                format("%s Username is empty or null.", getClass().getSimpleName())
        );

        return accountService.findAccountByUsernameOrEmail(username)
                .map(account ->
                        User.builder()
                                .username(account.getUsername())
                                .password(account.getPassword())
                                .disabled(!account.isEnabled())
                                .authorities(new SimpleGrantedAuthority(account.getRole().name()))
                                .build()
                ).orElseThrow(() -> new UsernameNotFoundException(
                                BAD_REQUEST,
                                format("%s User with username %s does`t exists.", getClass().getSimpleName(), username)
                        )
                );
    }
}
