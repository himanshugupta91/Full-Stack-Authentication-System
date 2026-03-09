package com.auth.security.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter that intercepts requests and validates JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Validates incoming bearer token and sets authenticated user context when
     * token is valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractBearerToken(request);
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                authenticateBearerToken(request, jwt);
            }
        } catch (Exception exception) {
            logger.error("Cannot set user authentication", exception);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     */
    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String bearerToken = authorizationHeader.substring("Bearer ".length());
        return bearerToken;
    }

    private void authenticateBearerToken(HttpServletRequest request, String jwt) {
        String email = jwtUtil.getEmailFromToken(jwt);
        List<String> roles = jwtUtil.getRolesFromToken(jwt);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(email, "", authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                jwt,
                authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (logger.isDebugEnabled()) {
            logger.debug("Stateless authenticated user: " + email + " with authorities: " + authorities);
        }
    }
}
