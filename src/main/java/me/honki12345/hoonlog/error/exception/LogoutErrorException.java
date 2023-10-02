package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class LogoutErrorException extends CustomBaseException {

    public LogoutErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
