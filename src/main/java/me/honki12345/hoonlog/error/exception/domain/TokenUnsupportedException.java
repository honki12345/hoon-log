package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.security.JwtException;

public class TokenUnsupportedException extends JwtException {

    public TokenUnsupportedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
