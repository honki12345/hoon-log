package me.honki12345.hoonlog.util;

import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPost;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestUserAccountRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.service.OptimisticTestPostLikeService;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestUserAccountRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.service.PessimisticTestPostLikeService;
import me.honki12345.hoonlog.config.ContainerShutDownListener;
import me.honki12345.hoonlog.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Import({ContainerShutDownListener.class, JpaAuditingConfig.class,
    OptimisticTestPostLikeService.class, PessimisticTestPostLikeService.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@DataJpaTest
public abstract class ConcurrencyTestSupport {

}
