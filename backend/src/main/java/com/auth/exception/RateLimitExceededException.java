package com.auth.exception;

import lombok.Getter;

/**
 * Thrown when request frequency exceeds configured rate limits.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;
    /**
     * Creates a new RateLimitExceededException instance.
     */

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
