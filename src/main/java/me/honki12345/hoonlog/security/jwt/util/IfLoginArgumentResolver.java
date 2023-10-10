package me.honki12345.hoonlog.security.jwt.util;

import java.util.Collection;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class IfLoginArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean b = parameter.getParameterAnnotation(IfLogin.class) != null
            && parameter.getParameterType() == UserAccountPrincipal.class;
        return b;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = null;
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken authenticationToken)) {
            return null;
        }

        Object principal = authenticationToken.getPrincipal();

        if (principal == null) {
            return null;
        }
        UserAccountPrincipal userAccountPrincipal = (UserAccountPrincipal) principal;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String role = grantedAuthority.getAuthority();
            userAccountPrincipal.addRole(role);
        }

        return userAccountPrincipal;
    }
}
