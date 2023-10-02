package me.honki12345.hoonlog.error.exception.security;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class LoginErrorException extends CustomBaseException {

    public LoginErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
