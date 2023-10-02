package me.honki12345.hoonlog.dto.security;

import java.util.ArrayList;
import java.util.List;

public record LoginUserDTO(
    Long userId,
    String username,
    List<String> roles
) {

    public void addRole(String role) {
        roles.add(role);
    }

    public static LoginUserDTO of(Long userId, String username) {
        return new LoginUserDTO(userId, username, new ArrayList<>());
    }
}
