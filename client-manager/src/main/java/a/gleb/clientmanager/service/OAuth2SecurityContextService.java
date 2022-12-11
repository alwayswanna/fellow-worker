package a.gleb.clientmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OAuth2SecurityContextService {


    public void extractUserOAuth2Principal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Message: {}, ", authentication);
    }

}
