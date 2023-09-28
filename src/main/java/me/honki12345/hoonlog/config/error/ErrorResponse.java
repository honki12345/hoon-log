package me.honki12345.hoonlog.config.error;

public record ErrorResponse(
    String message,
    String code
) {

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code.getMessage(), code.getCode());
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(message, code.getCode());
    }
}
