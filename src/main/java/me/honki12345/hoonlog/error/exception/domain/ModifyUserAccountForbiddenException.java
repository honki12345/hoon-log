package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;

public class ModifyUserAccountForbiddenException extends ForbiddenException {

    public ModifyUserAccountForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
