package me.honki12345.hoonlog.dto.security;

import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserAccountPrincipal(
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static UserAccountPrincipal from(UserAccount entity) {
        return new UserAccountPrincipal(
            entity.getUsername(),
            entity.getUserPassword(),
            List.of(new SimpleGrantedAuthority("USER"))
        );

    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
