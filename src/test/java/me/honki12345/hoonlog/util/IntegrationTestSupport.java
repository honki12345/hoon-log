package me.honki12345.hoonlog.util;

import me.honki12345.hoonlog.config.ContainerShutDownListener;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import me.honki12345.hoonlog.util.FileHelper;
import me.honki12345.hoonlog.util.PostBuilder;
import me.honki12345.hoonlog.util.PostCommentBuilder;
import me.honki12345.hoonlog.util.TagBuilder;
import me.honki12345.hoonlog.util.TokenBuilder;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({ContainerShutDownListener.class, JpaAuditingConfig.class, FileHelper.class, PostBuilder.class,
PostCommentBuilder.class, TagBuilder.class, TokenBuilder.class, UserAccountBuilder.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {
}
