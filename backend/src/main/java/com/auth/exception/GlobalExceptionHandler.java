package com.auth.exception;

import com.auth.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralised exception handler that translates domain exceptions into
 * consistent {@link ApiResponse} error payloads for all REST controllers.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Handles bean-validation errors from {@code @Valid} annotations. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = buildValidationMessage(ex.getBindingResult().getFieldErrors());
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /** Handles login failures — returns a generic 401 to avoid information disclosure. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password!"));
    }

    /** Handles duplicate-email registration attempts. */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Handles look-up failures for users, roles, and other entities. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Handles invalid or expired JWT / reset tokens. */
    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenValidationException(TokenValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Handles temporary account lockouts and sets a {@code Retry-After} header. */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountLockedException(AccountLockedException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));
        return ResponseEntity.status(HttpStatus.LOCKED)
                .headers(headers)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Handles request-rate limit violations and sets a {@code Retry-After} header. */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitExceededException(RateLimitExceededException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Handles client-side validation errors raised from the service layer. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    /** Catch-all handler — logs unexpected exceptions and returns a safe 500 response. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled exception in API layer", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }
    /**
     * Builds validation message.
     */

    private String buildValidationMessage(List<FieldError> fieldErrors) {
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(", "));
        return StringUtils.hasText(message) ? message : "Validation failed.";
    }
}
