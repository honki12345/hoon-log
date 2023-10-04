package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class PostCommentNotFoundException extends CustomBaseException {

    public PostCommentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
