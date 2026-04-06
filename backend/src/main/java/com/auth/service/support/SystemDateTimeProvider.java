package com.auth.service.support;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Production implementation of {@link DateTimeProvider}.
 */
@Component
public class SystemDateTimeProvider implements DateTimeProvider {
    /**
     * Returns this operation.
     */

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
