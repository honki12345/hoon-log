package me.honki12345.hoonlog.util;

import me.honki12345.hoonlog.config.ContainerShutDownListenerConfig;
import me.honki12345.hoonlog.config.ElasticTestContainerConfig;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({ContainerShutDownListenerConfig.class, JpaAuditingConfig.class, FileHelper.class, PostBuilder.class,
PostCommentBuilder.class, TagBuilder.class, TokenBuilder.class, UserAccountBuilder.class, ElasticTestContainerConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {
}
