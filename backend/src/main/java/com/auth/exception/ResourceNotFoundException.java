package com.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /** Creates a not-found exception with a caller-provided message. */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
