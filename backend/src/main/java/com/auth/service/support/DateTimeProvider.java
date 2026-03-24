package com.auth.service.support;

import java.time.LocalDateTime;

/**
 * Abstraction for current time retrieval to keep business logic deterministic.
 */
public interface DateTimeProvider {

    LocalDateTime now();
}
