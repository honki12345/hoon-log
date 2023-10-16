package me.honki12345.hoonlog.config;

import java.util.Optional;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Profile("test")
@TestConfiguration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(UserAccountBuilder.TEST_USERNAME);
    }
}
