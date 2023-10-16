package me.honki12345.hoonlog.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO")
public record LoginResponse(
    @Schema(description = "액세스 토큰")
    String accessToken,
    @Schema(description = "리프레쉬 토큰")
    String refreshToken,
    @Schema(description = "회원번호")
    Long userId,
    @Schema(description = "아이디")
    String username
) {

    public static LoginResponse of(String accessToken, String refreshToken, Long userId,
        String username) {
        return new LoginResponse(accessToken, refreshToken, userId, username);
    }

}
