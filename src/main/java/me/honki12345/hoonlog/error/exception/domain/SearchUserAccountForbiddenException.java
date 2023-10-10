package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;

public class SearchUserAccountForbiddenException extends ForbiddenException {

    public SearchUserAccountForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
