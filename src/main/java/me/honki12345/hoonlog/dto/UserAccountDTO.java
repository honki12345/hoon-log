package me.honki12345.hoonlog.dto;

import me.honki12345.hoonlog.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDTO(
    Long id,
    String userId,
    String userPassword,
    String email,
    ProfileDTO profileDTO,
    LocalDateTime createdAt
) {

    public static UserAccountDTO from(UserAccount entity) {
        return new UserAccountDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getUserPassword(),
            entity.getEmail(),
            ProfileDTO.from(entity.getProfile()),
            entity.getCreatedAt()
        );
    }
}
