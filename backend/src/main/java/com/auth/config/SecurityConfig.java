package com.auth.config;

import com.auth.security.CustomUserDetailsService;
import com.auth.security.jwt.JwtAuthFilter;
import com.auth.security.oauth2.LinkedInAuthorizationRequestResolver;
import com.auth.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.auth.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final OAuth2AuthenticationSuccessHandler successHandler;
        private final OAuth2AuthenticationFailureHandler failureHandler;
        private final LinkedInAuthorizationRequestResolver linkedInAuthorizationRequestResolver;

        /**
         * Configures stateless security, endpoint authorization, OAuth2, and JWT filter
         * order.
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> {
                                        headers.contentTypeOptions(Customizer.withDefaults());
                                        headers.frameOptions(frame -> frame.deny());
                                        headers.referrerPolicy(referrer -> referrer
                                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER));
                                        headers.permissionsPolicy(permissions -> permissions
                                                        .policy("camera=(), microphone=(), geolocation=()"));
                                        headers.httpStrictTransportSecurity(hsts -> hsts
                                                        .includeSubDomains(true)
                                                        .maxAgeInSeconds(31536000));
                                })
                                .sessionManagement(session ->
                                // OAuth2 login requires temporary session storage for state/nonce validation.
                                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(ApiPaths.AUTH_V1 + "/**").permitAll()
                                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                                .requestMatchers(ApiPaths.ADMIN_V1 + "/**").hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(ApiPaths.USER_V1 + "/**")
                                                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> oauth
                                                .authorizationEndpoint(
                                                                endpoint -> endpoint.authorizationRequestResolver(
                                                                                linkedInAuthorizationRequestResolver))
                                                .successHandler(successHandler)
                                                .failureHandler(failureHandler))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /** Exposes AuthenticationManager from Spring's AuthenticationConfiguration. */
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        /**
         * Builds DAO authentication provider with explicit user details service and
         * encoder.
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider(
                        CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }
}
