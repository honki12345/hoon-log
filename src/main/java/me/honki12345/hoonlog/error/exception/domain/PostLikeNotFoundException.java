package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.NotFoundException;

public class PostLikeNotFoundException extends NotFoundException {

    public PostLikeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
