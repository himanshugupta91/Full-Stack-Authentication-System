package com.auth.security.oauth2;

import jakarta.annotation.PostConstruct;
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

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    private String frontendLoginUrl;

    @PostConstruct
    void initializeRedirectTarget() {
        frontendLoginUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/login")
                .build(true)
                .toUriString();
    }

    /**
     * Redirects failed OAuth2 attempts back to frontend login with the error
     * message.
     */
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
                .fromUriString(frontendLoginUrl)
                .queryParam("oauthError", errorMessage)
                .build()
                .encode()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String resolveErrorMessage(AuthenticationException exception) {
        String message = exception.getMessage();
        String resolvedMessage;
        if (message == null || message.isBlank()) {
            resolvedMessage = "OAuth login failed.";
        } else if (message.length() <= 240) {
            resolvedMessage = message;
        } else {
            resolvedMessage = message.substring(0, 240);
        }
        return resolvedMessage;
    }

    private Throwable resolveRootCause(AuthenticationException exception) {
        Throwable cause = exception.getCause();
        Throwable rootCause;
        if (cause == null) {
            rootCause = exception;
        } else {
            rootCause = cause;
        }
        return rootCause;
    }
}
