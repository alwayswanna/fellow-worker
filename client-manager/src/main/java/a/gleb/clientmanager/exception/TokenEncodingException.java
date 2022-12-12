package a.gleb.clientmanager.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class TokenEncodingException extends ResponseStatusException {
    public TokenEncodingException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
