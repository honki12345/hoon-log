package me.honki12345.hoonlog.dto.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.UserAccountDTO;

public record UserAccountPrincipal(
    Long userId,
    String username,
    List<String> roles
) {

    public void addRole(String role) {
        roles.add(role);
    }

    public static UserAccountPrincipal of(Long userId, String username) {
        return new UserAccountPrincipal(userId, username, new ArrayList<>());
    }


    public UserAccountDTO toDTO() {
        return UserAccountDTO.of(userId, username, roles);
    }

    public static UserAccountPrincipal from(UserAccountDTO dto) {
        List<String> roles = dto.roles().stream().map(
            Role::getName).collect(
            Collectors.toList());
        return new UserAccountPrincipal(dto.id(), dto.username(), roles);
    }
}
