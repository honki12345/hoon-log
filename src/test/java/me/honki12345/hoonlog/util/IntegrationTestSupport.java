package me.honki12345.hoonlog.util;

import me.honki12345.hoonlog.config.ContainerShutDownListener;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({TestUtils.class, ContainerShutDownListener.class, JpaAuditingConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {


}
