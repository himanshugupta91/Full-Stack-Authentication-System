package com.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenValidationException extends RuntimeException {
    /** Creates an unauthorized exception with a caller-provided message. */
    public TokenValidationException(String message) {
        super(message);
    }
}
