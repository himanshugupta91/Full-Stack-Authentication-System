package com.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    /** Creates a conflict exception with a caller-provided message. */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
