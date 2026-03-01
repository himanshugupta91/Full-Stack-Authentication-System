package com.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.stereotype.Component;

/**
 * Customizes OAuth2 authorization requests for LinkedIn compatibility.
 */
@Component
public class LinkedInAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String AUTHORIZATION_BASE_URI = "/oauth2/authorization";
    private static final String LINKEDIN_REGISTRATION_ID = "linkedin";
    private static final String LINKEDIN_PATH_SUFFIX = "/" + LINKEDIN_REGISTRATION_ID;

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public LinkedInAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                AUTHORIZATION_BASE_URI);
    }

    /** Resolves provider authorization request from incoming servlet request. */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);
        return customizeForLinkedIn(isLinkedInRequest(request), authorizationRequest);
    }

    /** Resolves provider authorization request for an explicit client registration id. */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);
        return customizeForLinkedIn(LINKEDIN_REGISTRATION_ID.equals(clientRegistrationId), authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeForLinkedIn(boolean isLinkedInRequest,
            OAuth2AuthorizationRequest authorizationRequest) {
        if (!isLinkedInRequest || authorizationRequest == null) {
            return authorizationRequest;
        }

        return removeNonce(authorizationRequest);
    }

    private boolean isLinkedInRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri != null && requestUri.endsWith(LINKEDIN_PATH_SUFFIX);
    }

    /** Removes nonce parameter/attribute for LinkedIn to prevent nonce validation mismatches. */
    private OAuth2AuthorizationRequest removeNonce(OAuth2AuthorizationRequest authorizationRequest) {
        boolean hasNonce = authorizationRequest.getAdditionalParameters().containsKey(OidcParameterNames.NONCE)
                || authorizationRequest.getAttributes().containsKey(OidcParameterNames.NONCE);
        if (!hasNonce) {
            return authorizationRequest;
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(params -> params.remove(OidcParameterNames.NONCE))
                .attributes(attrs -> attrs.remove(OidcParameterNames.NONCE))
                .build();
    }
}
