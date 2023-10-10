package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.security.JwtException;

public class TokenExpiredException extends JwtException {

    public TokenExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
