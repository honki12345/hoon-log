package me.honki12345.hoonlog.dto.security;

public record LoginInfoDTO(
    Long userId,
    String username
) {

    public static LoginInfoDTO of(Long userId, String username) {
        return new LoginInfoDTO(userId, username);
    }

}
