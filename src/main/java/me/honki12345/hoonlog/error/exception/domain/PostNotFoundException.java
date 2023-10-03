package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class PostNotFoundException extends CustomBaseException {

    public PostNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
