package me.honki12345.hoonlog.config;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.repository.PostRepositoryOptimisticLock;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.repository.elasticsearch.PostSearchRepository;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("test")
@TestConfiguration
@EnableJpaRepositories(
    basePackageClasses = {UserAccountRepository.class, PostRepositoryOptimisticLock.class},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {PostSearchRepository.class}
    ))
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(UserAccountBuilder.TEST_USERNAME);
    }
}
