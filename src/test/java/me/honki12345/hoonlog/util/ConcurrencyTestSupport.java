package me.honki12345.hoonlog.util;

import me.honki12345.hoonlog.concurrency.service.PostLikeServiceConcurrencyTest;
import me.honki12345.hoonlog.config.ContainerShutDownListener;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Import({ContainerShutDownListener.class, JpaAuditingConfig.class,
    PostLikeServiceConcurrencyTest.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@DataJpaTest
public abstract class ConcurrencyTestSupport {

}
