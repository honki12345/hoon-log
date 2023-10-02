package me.honki12345.hoonlog.security.jwt.provider;

import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.security.LoginInfoDTO;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenizer jwtTokenizer;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        Claims claims = jwtTokenizer.parseAccessToken(authenticationToken.getToken());
        Long userId = claims.get("id", Long.class);
        String username = claims.get("name", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        LoginInfoDTO loginInfoDTO = LoginInfoDTO.of(userId, username);
        return new JwtAuthenticationToken(authorities, loginInfoDTO, null);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
