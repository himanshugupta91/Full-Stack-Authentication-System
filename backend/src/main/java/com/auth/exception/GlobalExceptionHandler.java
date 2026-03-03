package com.auth.exception;

import com.auth.dto.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (e.g. @Valid annotations).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = buildValidationErrorMessage(ex.getBindingResult().getFieldErrors());
        return ResponseEntity.badRequest().body(new MessageResponse(errorMessage, false));
    }

    /**
     * Handle bad credentials (login failure).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid email or password!", false));
    }

    /**
     * Handle UserAlreadyExistsException.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle TokenValidationException.
     */
    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<MessageResponse> handleTokenValidationException(TokenValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle temporary account lockout errors.
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<MessageResponse> handleAccountLockedException(AccountLockedException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));
        return ResponseEntity.status(HttpStatus.LOCKED)
                .headers(headers)
                .body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle request-rate limit violations.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<MessageResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers)
                .body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle client-side validation/argument errors raised from service layer.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage(), false));
    }

    /**
     * Handle generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception ex) {
        log.error("Unhandled exception in API layer", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An unexpected error occurred. Please try again later.", false));
    }

    private String buildValidationErrorMessage(List<FieldError> fieldErrors) {
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(", "));

        if (!StringUtils.hasText(message)) {
            return "Validation failed.";
        }
        return message;
    }
}
