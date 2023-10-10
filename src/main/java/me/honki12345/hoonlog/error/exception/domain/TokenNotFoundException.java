package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.NotFoundException;

public class TokenNotFoundException extends NotFoundException {

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
