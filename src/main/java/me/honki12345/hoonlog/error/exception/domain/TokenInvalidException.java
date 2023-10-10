package me.honki12345.hoonlog.error.exception.domain;

import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.security.JwtException;

public class TokenInvalidException extends JwtException {

    public TokenInvalidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
