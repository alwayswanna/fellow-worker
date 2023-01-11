/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UsernameNotFoundException extends ResponseStatusException {

    public UsernameNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
