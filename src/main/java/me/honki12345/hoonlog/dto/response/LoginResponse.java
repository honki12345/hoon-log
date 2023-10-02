package me.honki12345.hoonlog.dto.response;

public record LoginResponse(
    String accessToken,
    String refreshToken,

    Long userId,
    String username
) {

    public static LoginResponse of(String accessToken, String refreshToken, Long userId,
        String username) {
        return new LoginResponse(accessToken, refreshToken, userId, username);
    }

}
