package me.honki12345.hoonlog.config.error.exception;

import me.honki12345.hoonlog.config.error.ErrorCode;

public class UserAccountNotFoundException extends CustomBaseException {

    public UserAccountNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
