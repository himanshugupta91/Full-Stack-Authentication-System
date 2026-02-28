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

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /** Redirects failed OAuth2 attempts back to frontend login with the error message. */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = exception.getMessage() == null ? "OAuth login failed." : exception.getMessage();
        String provider = request.getParameter("registrationId");
        Throwable rootCause = exception.getCause() == null ? exception : exception.getCause();
        log.debug("OAuth2 authentication failed. uri={}, providerHint={}, message={}, rootCause={}",
                request.getRequestURI(),
                provider,
                errorMessage,
                rootCause.getMessage());

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/login")
                .queryParam("oauthError", errorMessage)
                .build()
                .encode()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
