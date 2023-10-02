package me.honki12345.hoonlog.error.exception;

import me.honki12345.hoonlog.error.ErrorCode;

public class JwtException extends CustomBaseException {

    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
