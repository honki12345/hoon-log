package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;

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

    public static UserAccountDTO of(Long userId, String username, List<String> roles) {
        Set<Role> roleSet = roles.stream().map(Role::of).collect(Collectors.toSet());
        return new UserAccountDTO(userId, username, null, null, null, null, roleSet);
    }

    public UserAccountPrincipal toPrincipal() {
        return new UserAccountPrincipal(
            id(),
            username(),
            roles().stream().map(Role::getName).collect(Collectors.toList()));
    }
}
