package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;

public class DeleteCommentForbiddenException extends ForbiddenException {

    public DeleteCommentForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
