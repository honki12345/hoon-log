package me.honki12345.hoonlog.config;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.security.filter.JwtAuthenticationFilter;
import me.honki12345.hoonlog.security.jwt.CustomAuthenticationEntryPoint;
import me.honki12345.hoonlog.security.jwt.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends
    AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(
            AuthenticationManager.class);

        builder.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(jwtAuthenticationProvider);
    }
}
