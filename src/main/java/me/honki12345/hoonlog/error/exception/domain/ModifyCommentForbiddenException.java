package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;

public class ModifyCommentForbiddenException extends ForbiddenException {

    public ModifyCommentForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
