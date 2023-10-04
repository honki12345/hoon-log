package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.NotFoundException;

public class PostCommentNotFoundException extends NotFoundException {

    public PostCommentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
