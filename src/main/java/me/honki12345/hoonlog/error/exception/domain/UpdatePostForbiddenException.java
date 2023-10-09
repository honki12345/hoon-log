package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;

public class UpdatePostForbiddenException extends ForbiddenException {

    public UpdatePostForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
