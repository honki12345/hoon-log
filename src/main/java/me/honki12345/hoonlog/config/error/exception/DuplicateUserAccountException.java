package me.honki12345.hoonlog.config.error.exception;

import me.honki12345.hoonlog.config.error.ErrorCode;

public class DuplicateUserAccountException extends CustomBaseException{
    public DuplicateUserAccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
