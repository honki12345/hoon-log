package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.honki12345.hoonlog.domain.RefreshToken;

@Schema(description = "JWT DTO")
public record TokenDTO(
    @Schema(description = "액세스 토큰")
    String accessToken,
    @Schema(description = "리프레쉬 토큰")
    String refreshToken
) {

    public static TokenDTO of(String accessToken, String refreshToken) {
        return new TokenDTO(accessToken, refreshToken);
    }

    public static TokenDTO from(RefreshToken entity) {
        return new TokenDTO(null, entity.getToken());
    }
}
