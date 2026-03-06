package com.auth.config;

/**
 * Central API path constants used for URI versioning.
 */
public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String API_BASE = "/api";
    public static final String API_V1_BASE = API_BASE + "/v1";

    public static final String AUTH_V1 = API_V1_BASE + "/auth";
    public static final String USER_V1 = API_V1_BASE + "/user";
    public static final String ADMIN_V1 = API_V1_BASE + "/admin";
}
