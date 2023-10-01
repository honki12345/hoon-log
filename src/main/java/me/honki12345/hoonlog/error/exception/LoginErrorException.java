package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class LoginErrorException extends CustomBaseException {

    public LoginErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
