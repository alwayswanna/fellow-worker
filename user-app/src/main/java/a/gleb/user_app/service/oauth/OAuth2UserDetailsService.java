package a.gleb.user_app.service.oauth;

import a.gleb.user_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuth2UserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var userByUsername = userService.findUserByUsername(username);
        if (userByUsername == null) {
            throw new UsernameNotFoundException("user with login=%s does not exist".formatted(username));
        }

        return User.builder()
                .username(username)
                .password(userByUsername.getPassword())
                .disabled(false)
                .accountExpired(false)
                .roles(userByUsername.getRole().getCode())
                .build();
    }
}
