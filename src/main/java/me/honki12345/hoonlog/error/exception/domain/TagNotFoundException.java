package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.NotFoundException;

public class TagNotFoundException extends NotFoundException {

    public TagNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
