package com.auth.security;

import com.auth.dto.AuthTokens;
import com.auth.entity.User;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.auth.OAuth2UserProvisioningService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Handles OAuth2 success by creating/finding a local user and issuing application tokens.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String OAUTH2_CALLBACK_PATH = "/oauth2/callback";

    private final OAuth2UserProvisioningService oAuth2UserProvisioningService;

    private final AuthTokenService authTokenService;

    private final RefreshTokenCookieService refreshTokenCookieService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /** Provisions local user data and redirects to frontend callback with a fresh access token. */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();

        User user = oAuth2UserProvisioningService.loadOrCreateUser(oauthToken, oauth2User);
        AuthTokens tokenResult = authTokenService.issueTokens(user);

        response.addHeader(HttpHeaders.SET_COOKIE,
                refreshTokenCookieService.buildRefreshTokenCookie(tokenResult.refreshToken()));

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + OAUTH2_CALLBACK_PATH)
                .build(true)
                .toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
