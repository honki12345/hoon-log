package me.honki12345.hoonlog.security.jwt.util;

import java.util.Collection;
import java.util.Iterator;
import me.honki12345.hoonlog.dto.security.LoginInfoDTO;
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
        return parameter.getParameterAnnotation(IfLogin.class) != null
            && parameter.getParameterType() == UserAccountPrincipal.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = null;
        try {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception e) {
            return null;
        }

        if (authentication == null) {
            return null;
        }

        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        Object principal = authenticationToken.getPrincipal();

        if (principal == null) {
            return null;
        }
        UserAccountPrincipal userAccountPrincipal = (UserAccountPrincipal) principal;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            GrantedAuthority grantedAuthority = iterator.next();
            String role = grantedAuthority.getAuthority();
            userAccountPrincipal.addRole(role);
        }

        return userAccountPrincipal;
    }
}
