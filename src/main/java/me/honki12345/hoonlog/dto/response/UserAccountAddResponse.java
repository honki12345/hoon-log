package me.honki12345.hoonlog.dto.response;

import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

public record UserAccountAddResponse(
        Long id,
        String userId,
        String email,
        LocalDateTime createdAt
) {
    public static UserAccountAddResponse from(UserAccountDTO dto) {
        return new UserAccountAddResponse(dto.id(), dto.userId(), dto.email(), dto.createdAt());
    }
}
