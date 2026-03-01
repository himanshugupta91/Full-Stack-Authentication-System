package com.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts requests and validates JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    /**
     * Validates incoming bearer token and sets authenticated user context when
     * token is valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                authenticateRequest(request, jwt);
            }
        } catch (Exception exception) {
            logger.error("Cannot set user authentication", exception);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        boolean hasHeader = StringUtils.hasText(headerAuth);
        if (hasHeader) {
            boolean isBearer = headerAuth.startsWith(BEARER_PREFIX);
            if (isBearer) {
                return headerAuth.substring(BEARER_PREFIX_LENGTH);
            }
        }

        return null;
    }

    private void authenticateRequest(HttpServletRequest request, String jwt) {
        String email = jwtUtil.getEmailFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (logger.isDebugEnabled()) {
            logger.debug("Authenticated user: " + email + " with authorities: " + userDetails.getAuthorities());
        }
    }
}
