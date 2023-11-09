package me.honki12345.hoonlog.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.security.jwt.CustomAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
            .httpBasic(HttpBasicConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable)
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/auth/token")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/users/{\\d+}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/v1/users/{\\d+}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/posts}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/posts/{\\d+}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/posts/{\\d+}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/comments}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/posts/like}")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/posts/like}")).authenticated()
                .anyRequest().permitAll())
            .exceptionHandling(
                configurer -> configurer.authenticationEntryPoint(customAuthenticationEntryPoint))
            .apply(authenticationManagerConfig);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
