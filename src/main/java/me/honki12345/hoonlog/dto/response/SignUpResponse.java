package me.honki12345.hoonlog.dto.response;

import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

public record SignUpResponse(
        Long id,
        String userId,
        String userPassword,
        String email,
        LocalDateTime createdAt
) {
    public static SignUpResponse from(UserAccountDTO dto) {
        return new SignUpResponse(dto.id(), dto.userId(), dto.userPassword(), dto.email(), dto.createdAt());
    }
}
