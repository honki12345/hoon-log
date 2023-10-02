package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class RoleNotFoundException extends CustomBaseException {

    public RoleNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
