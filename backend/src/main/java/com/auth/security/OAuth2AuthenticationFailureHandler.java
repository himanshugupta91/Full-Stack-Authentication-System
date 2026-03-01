package com.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Redirects frontend with OAuth2 login error details.
 */
@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String LOGIN_PATH = "/login";
    private static final String OAUTH_ERROR_QUERY_PARAM = "oauthError";
    private static final String FALLBACK_ERROR_MESSAGE = "OAuth login failed.";

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /** Redirects failed OAuth2 attempts back to frontend login with the error message. */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = resolveErrorMessage(exception);
        String provider = request.getParameter("registrationId");
        Throwable rootCause = resolveRootCause(exception);
        log.debug("OAuth2 authentication failed. uri={}, providerHint={}, message={}, rootCause={}",
                request.getRequestURI(),
                provider,
                errorMessage,
                rootCause.getMessage());

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + LOGIN_PATH)
                .queryParam(OAUTH_ERROR_QUERY_PARAM, errorMessage)
                .build()
                .encode()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String resolveErrorMessage(AuthenticationException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return FALLBACK_ERROR_MESSAGE;
        }
        return message;
    }

    private Throwable resolveRootCause(AuthenticationException exception) {
        Throwable cause = exception.getCause();
        if (cause == null) {
            return exception;
        }
        return cause;
    }
}
