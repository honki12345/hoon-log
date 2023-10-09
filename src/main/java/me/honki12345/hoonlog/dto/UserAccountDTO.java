package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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


    public UserAccountDTO changePassword(String newUserPassword) {
        return new UserAccountDTO(this.id, this.username, newUserPassword, this.email,
            this.profileDTO, this.createdAt, this.roles);
    }

    public UserAccountDTO addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public static UserAccountDTO of(String username, String password) {
        return new UserAccountDTO(null, username, password, null, null, null, null);
    }

    public static UserAccountDTO of(ProfileDTO profile) {
        return new UserAccountDTO(null, null, null, null, profile, null, null);
    }

    public static UserAccountDTO of(Long userId, String username, String userPassword, String email,
        ProfileDTO profileDTO) {
        return new UserAccountDTO(userId, username, userPassword, email, profileDTO, null,
            new HashSet<>());
    }

    public static UserAccountDTO of(Long userId, String username, List<String> roles) {
        Set<Role> roleSet = roles.stream().map(Role::of).collect(Collectors.toSet());
        return new UserAccountDTO(userId, username, null, null, null, null, roleSet);
    }

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

    public UserAccount toEntity() {
        return UserAccount.of(username, userPassword, email, profileDTO.toVO());
    }
}
