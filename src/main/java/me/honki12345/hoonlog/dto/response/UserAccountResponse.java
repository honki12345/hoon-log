package me.honki12345.hoonlog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

public record UserAccountResponse(
    Long id,
    String username,
    String email,
    @JsonProperty("profile")
    ProfileDTO profileDTO,
    LocalDateTime createdAt,
    Set<Role> roles
) {

    public static UserAccountResponse from(UserAccountDTO dto) {
        return new UserAccountResponse(dto.id(), dto.username(), dto.email(), dto.profileDTO(),
            dto.createdAt(), dto.roles());
    }
}
