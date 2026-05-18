package com.zayden.profile_service.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(1001, "You do not have permission!", HttpStatus.FORBIDDEN),
    PARSE_EXCEPTION(1002, "Parse exception.", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_AVATAR(1003, "Can not update avatar!", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_NOT_EXISTED(1004, "Profile is not existed!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1006, "Invalid message key.", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1007, "Username must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1008, "Password must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Invalid email!", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1009, "Email is required!", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1010, "Your age must be at least {min}!", HttpStatus.BAD_REQUEST),
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
