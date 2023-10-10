package me.honki12345.hoonlog.config;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

class JpaConfigTest {

/*
    @Autowired
    JpaConfig jpaConfig;

    @Autowired
    AuditorAware<String> auditorAware;

    @DisplayName("")
    @Test
    void test() {
        // given
        SecurityContext context = SecurityContextHolder.getContext();
        String username = "test";
        context.setAuthentication(
            new JwtAuthenticationToken(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                UserAccountPrincipal.of(1L, username),
                null)
        );

        // when
        String actual = auditorAware.getCurrentAuditor().orElseThrow();

        // then
        assertThat(jpaConfig).isNotNull();
        assertThat(auditorAware).isNotNull();
        assertThat(actual).isEqualTo(username);

    }
*/


    @DisplayName("")
    @Test
    void test() throws Exception {
        // given
        Class<?> jpaConfigClass = Class.forName("me.honki12345.hoonlog.config.JpaConfig");
        Constructor<?> constructor = jpaConfigClass.getConstructor();
        Object instance = constructor.newInstance();
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
            new JwtAuthenticationToken(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                UserAccountPrincipal.of(1L, TestUtils.TEST_USERNAME),
                null
            )
        );

        // when

        JpaConfig jpaConfig = (JpaConfig) instance;
        String actual = jpaConfig.auditorAware().getCurrentAuditor().orElseThrow();

        // then
        assertThat(jpaConfigClass).isNotNull();
        assertThat(jpaConfig).isNotNull();
        assertThat(actual).isEqualTo(TestUtils.TEST_USERNAME);
    }
}