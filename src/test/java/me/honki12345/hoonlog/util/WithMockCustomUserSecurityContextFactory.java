package me.honki12345.hoonlog.util;

import java.util.List;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {


    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserAccountPrincipal userAccountPrincipal = UserAccountPrincipal.of(1L, "fpg123");
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER_ROLE"));
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authorities,
            userAccountPrincipal, null);
        context.setAuthentication(authenticationToken);
        return context;
    }
}
