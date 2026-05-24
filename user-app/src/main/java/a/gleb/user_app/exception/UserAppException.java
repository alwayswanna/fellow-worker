package a.gleb.user_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAppException extends RuntimeException {

    private final int statusCode;

    public UserAppException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode.value();
    }

    public UserAppException(String message) {
        super(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
