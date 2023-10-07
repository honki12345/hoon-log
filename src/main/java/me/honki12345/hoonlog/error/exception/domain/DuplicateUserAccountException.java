package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;
import me.honki12345.hoonlog.error.exception.DuplicateException;

public class DuplicateUserAccountException extends DuplicateException {

    public DuplicateUserAccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
