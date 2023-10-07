package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.DuplicateException;

public class DuplicatePostLikeException extends DuplicateException {

    public DuplicatePostLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
