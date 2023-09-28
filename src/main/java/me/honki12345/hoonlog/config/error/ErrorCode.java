package me.honki12345.hoonlog.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON1", "올바르지 않은 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON2", "잘못된 HTTP 메서드를 호출했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON3", "서버 에러가 발생했습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON4", "존재하지 않는 값입니다"),

    DUPLICATE_USER_ACCOUNT(HttpStatus.BAD_REQUEST, "USER1", "중복된 값이 존재합니다"),
    USER_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER2", "존재하지 않는 값입니다");

    private final String message;
    private final String code;
    private final HttpStatus status;

    ErrorCode(HttpStatus status, String code, String message) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
