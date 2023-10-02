package me.honki12345.hoonlog.dto;

import me.honki12345.hoonlog.domain.RefreshToken;

public record TokenDTO(
    String accessToken,
    String refreshToken
) {

    public static TokenDTO of(String accessToken, String refreshToken) {
        return new TokenDTO(accessToken, refreshToken);
    }

    public static TokenDTO from(RefreshToken entity) {
        return new TokenDTO(null, entity.getToken());
    }
}
