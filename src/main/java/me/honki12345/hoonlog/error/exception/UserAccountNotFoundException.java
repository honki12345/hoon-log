package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class UserAccountNotFoundException extends CustomBaseException {

    public UserAccountNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
