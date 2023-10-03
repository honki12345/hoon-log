package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class UserAccountNotFoundException extends CustomBaseException {

    public UserAccountNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
