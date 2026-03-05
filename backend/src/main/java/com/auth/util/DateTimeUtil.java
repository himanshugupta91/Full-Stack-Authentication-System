package com.auth.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Shared date-time utilities for consistently formatting timestamps in IST.
 */
public final class DateTimeUtil {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter IST_12_HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a", Locale.ENGLISH);

    private DateTimeUtil() {
    }

    public static LocalDateTime nowInIst() {
        return LocalDateTime.now(IST_ZONE);
    }

    public static String nowInIst12HourFormat() {
        return ZonedDateTime.now(IST_ZONE).format(IST_12_HOUR_FORMATTER);
    }

    public static String formatIst12Hour(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(IST_ZONE).format(IST_12_HOUR_FORMATTER);
    }
}
