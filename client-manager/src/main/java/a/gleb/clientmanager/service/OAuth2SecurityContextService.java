package a.gleb.clientmanager.service;

import a.gleb.clientmanager.exception.TokenEncodingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OAuth2SecurityContextService {


    public UUID getUserId() {
        return UUID.fromString(extractUserOAuth2Principal().get("user_id").toString());
    }

    private Map<String, Object> extractUserOAuth2Principal() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(context -> (JwtAuthenticationToken) context.getAuthentication())
                .map(JwtAuthenticationToken::getTokenAttributes)
                .orElseThrow(() -> new TokenEncodingException(
                        HttpStatus.UNAUTHORIZED, "Error while extract params from token")
                );
    }

}
