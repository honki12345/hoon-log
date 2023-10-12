package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;

@Schema(description = "회원정보 DTO")
public record UserAccountDTO(
    @Schema(description = "회원 번호")
    Long id,
    @Schema(description = "아이디")
    String username,
    @Schema(description = "비밀번호")
    String userPassword,
    @Schema(description = "이메일")
    String email,
    @Schema(description = "블로그 정보")
    ProfileDTO profileDTO,
    @Schema(description = "가입일자")
    LocalDateTime createdAt,
    @Schema(description = "권한")
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
        return UserAccount.of(null, username, userPassword, email, profileDTO.toVO(), roles);
    }
}
