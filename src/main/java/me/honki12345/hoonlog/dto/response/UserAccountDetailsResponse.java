package me.honki12345.hoonlog.dto.response;

import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

public record UserAccountDetailsResponse(
        Long id,
        String userId,
        String email,
        LocalDateTime createdAt
) {
    public static UserAccountDetailsResponse from(UserAccountDTO dto) {
        return new UserAccountDetailsResponse(dto.id(), dto.userId(), dto.email(), dto.createdAt());
    }
}
