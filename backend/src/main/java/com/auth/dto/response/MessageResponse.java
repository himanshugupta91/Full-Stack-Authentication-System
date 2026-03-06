package com.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic message response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private boolean success;
    private int code;

    /** Creates a successful response with only a message payload. */
    public MessageResponse(String message) {
        this(message, true, 200);
    }

    /** Creates a response and infers a default HTTP-like code from success flag. */
    public MessageResponse(String message, boolean success) {
        this(message, success, success ? 200 : 400);
    }
}
