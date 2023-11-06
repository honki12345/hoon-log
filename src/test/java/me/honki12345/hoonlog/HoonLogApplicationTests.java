package me.honki12345.hoonlog;

import static org.mockito.Mockito.mockStatic;

import me.honki12345.hoonlog.config.ElasticTestContainerConfig;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({ElasticTestContainerConfig.class, JpaAuditingConfig.class})
@SpringBootTest
class HoonLogApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void run() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

            mocked.when(() -> {
                    SpringApplication.run(HoonLogApplication.class,
                        new String[]{"foo", "bar"});
                })
                .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

            HoonLogApplication.main(new String[]{"foo", "bar"});

            mocked.verify(() -> {
                SpringApplication.run(HoonLogApplication.class,
                    new String[]{"foo", "bar"});
            });
        }
    }
}
