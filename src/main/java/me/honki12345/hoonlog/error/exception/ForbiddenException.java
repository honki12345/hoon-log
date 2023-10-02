package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class ForbiddenException extends CustomBaseException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
