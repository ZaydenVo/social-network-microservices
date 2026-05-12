package com.zayden.chat_service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(1001, "You do not have permission!", HttpStatus.FORBIDDEN),
    PARSE_EXCEPTION(1002, "Parse exception.", HttpStatus.BAD_REQUEST),
    PROFILE_NOT_FOUND(1004, "Profile is not found!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1006, "Invalid message key.", HttpStatus.BAD_REQUEST),
    ;
    int code;
    String message;
    HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
