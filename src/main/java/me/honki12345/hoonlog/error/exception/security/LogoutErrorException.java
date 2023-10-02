package me.honki12345.hoonlog.error.exception.security;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class LogoutErrorException extends CustomBaseException {

    public LogoutErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
