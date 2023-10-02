package me.honki12345.hoonlog.dto.security;

import java.util.ArrayList;
import java.util.List;

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
}
