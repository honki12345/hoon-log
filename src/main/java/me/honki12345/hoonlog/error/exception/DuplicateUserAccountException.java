package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class DuplicateUserAccountException extends CustomBaseException {

    public DuplicateUserAccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
