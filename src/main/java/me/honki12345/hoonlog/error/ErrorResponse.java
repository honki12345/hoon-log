package me.honki12345.hoonlog.error;

public record ErrorResponse(
    String message,
    String code
) {

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(message, code.getCode());
    }
}
