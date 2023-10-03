package me.honki12345.hoonlog.error.exception.security;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.CustomBaseException;

public class JwtException extends CustomBaseException {

    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
