package com.auth.exception;

import com.auth.dto.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

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
        StringBuilder messageBuilder = new StringBuilder();
        boolean firstMessageAdded = false;

        for (FieldError fieldError : fieldErrors) {
            String defaultMessage = fieldError.getDefaultMessage();
            if (defaultMessage == null || defaultMessage.isBlank()) {
                continue;
            }

            if (firstMessageAdded) {
                messageBuilder.append(", ");
            }

            messageBuilder.append(defaultMessage);
            firstMessageAdded = true;
        }

        if (!firstMessageAdded) {
            return "Validation failed.";
        }

        return messageBuilder.toString();
    }
}
