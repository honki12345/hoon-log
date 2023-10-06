package me.honki12345.hoonlog.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON1", "올바르지 않은 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON2", "잘못된 HTTP 메서드를 호출했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON3", "서버 에러가 발생했습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON4", "존재하지 않는 값입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON5", "권한이 없습니다"),

    DUPLICATE_USER_ACCOUNT(HttpStatus.BAD_REQUEST, "USER1", "중복된 값이 존재합니다"),
    USER_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER2", "존재하지 않는 값입니다"),

    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE1", "유저에게 줄 권한이 존재하지 않습니다"),

    LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "LOGIN1", "로그인에 실패하였습니다"),
    LOGOUT_ERROR(HttpStatus.BAD_REQUEST, "LOGOUT1", "로그아웃에 실패하였습니다"),

    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN1", "토큰을 찾을 수 없습니다"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOKEN2", "올바르지 않은 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN3", "토큰 유효기간이 지났습니다"),
    TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "TOKEN4", "지원하지 않는 토큰입니다"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST1", "게시글을 찾을 수 없습니다"),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT1", "댓글을 찾을 수 없습니다"),

    IMAGE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "IMAGE1", "파일 업로드에 실패하였습니다"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE2", "파일을 찾을 수 없습니다"),

    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG1", "태그를 찾을 수 없습니다");

    private final String message;
    private final String code;
    private final HttpStatus status;

    ErrorCode(HttpStatus status, String code, String message) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
