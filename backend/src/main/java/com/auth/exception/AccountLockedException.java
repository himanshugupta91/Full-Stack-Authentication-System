package com.auth.exception;

import lombok.Getter;

/**
 * Raised when a user account is temporarily locked due to repeated failed attempts.
 */
@Getter
public class AccountLockedException extends RuntimeException {

    private final long retryAfterSeconds;
    /**
     * Creates a new AccountLockedException instance.
     */

    public AccountLockedException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
