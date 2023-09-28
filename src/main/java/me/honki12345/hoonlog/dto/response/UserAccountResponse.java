package me.honki12345.hoonlog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

public record UserAccountResponse(
        Long id,
        String userId,
        String email,
        @JsonProperty("profile")
        ProfileDTO profileDTO,
        LocalDateTime createdAt
) {
    public static UserAccountResponse from(UserAccountDTO dto) {
        return new UserAccountResponse(dto.id(), dto.userId(), dto.email(), dto.profileDTO(), dto.createdAt());
    }
}
