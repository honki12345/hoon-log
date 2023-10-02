package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class CustomBaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomBaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
