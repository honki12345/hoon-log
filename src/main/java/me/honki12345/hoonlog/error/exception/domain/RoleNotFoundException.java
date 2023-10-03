package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class RoleNotFoundException extends CustomBaseException {

    public RoleNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
