package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class DuplicateException extends CustomBaseException {

    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
