package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.Set;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;

public record UserAccountDTO(
    Long id,
    String username,
    String userPassword,
    String email,
    ProfileDTO profileDTO,
    LocalDateTime createdAt,
    Set<Role> roles
) {

    public static UserAccountDTO from(UserAccount entity) {
        return new UserAccountDTO(
            entity.getId(),
            entity.getUsername(),
            entity.getUserPassword(),
            entity.getEmail(),
            ProfileDTO.from(entity.getProfile()),
            entity.getCreatedAt(),
            entity.getRoles()
        );
    }

}
