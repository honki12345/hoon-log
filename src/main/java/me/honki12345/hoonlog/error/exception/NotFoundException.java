package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class NotFoundException extends CustomBaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
