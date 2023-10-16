package me.honki12345.hoonlog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.util.List;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class JpaConfigTest {

    @DisplayName("jpaConfigTest")
    @Test
    void jpaConfigTest() throws Exception {
        // given
        Class<?> jpaConfigClass = Class.forName("me.honki12345.hoonlog.config.JpaConfig");
        Constructor<?> constructor = jpaConfigClass.getConstructor();
        Object instance = constructor.newInstance();
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
            new JwtAuthenticationToken(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                UserAccountPrincipal.of(1L, UserAccountBuilder.TEST_USERNAME),
                null
            )
        );

        // when

        JpaConfig jpaConfig = (JpaConfig) instance;
        String actual = jpaConfig.auditorAware().getCurrentAuditor().orElseThrow();

        // then
        assertThat(jpaConfigClass).isNotNull();
        assertThat(jpaConfig).isNotNull();
        assertThat(actual).isEqualTo(UserAccountBuilder.TEST_USERNAME);
    }
}