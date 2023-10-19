package me.honki12345.hoonlog.concurrency.optimisticlock;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_PASSWORD;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPost;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestUserAccountRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.service.OptimisticTestPostLikeService;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.util.ConcurrencyTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostLike Optimistic lock Test")
public class OptimisticPostLikeServiceConcurrencyTest extends ConcurrencyTestSupport {

    private final int threadCount = 300;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @Autowired
    OptimisticTestUserAccountRepository userAccountRepository;
    @Autowired
    OptimisticTestPostRepository postRepository;
    @Autowired
    OptimisticTestPostLikeRepository postLikeRepository;
    @Autowired
    OptimisticTestPostLikeService postLikeService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(threadCount);
        countDownLatch = new CountDownLatch(threadCount);
    }

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userAccountRepository.deleteAllInBatch();
    }


    @DisplayName("게시글에 좋아요 요청이 동시에 이루어질 경우, 게시글의 좋아요 카운트에 낙관적 락을 걸었다면, 데이터 무결성을 보장한다")
    @Test
    void givenMultiThreadingRequest_whenOptimisticLockOnPostLike_thenReturnsLikeCountSameAsMultiThread() throws InterruptedException {
        // given
        OptimisticTestUserAccount testUserAccount = OptimisticTestUserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com");
        userAccountRepository.save(testUserAccount);
        OptimisticTestPost testPost = OptimisticTestPost.of(null, testUserAccount, "title", "content");
        OptimisticTestPost savedTestPost = postRepository.save(testPost);
        AtomicInteger conflictCount = new AtomicInteger();

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                OptimisticTestUserAccount userAccount = OptimisticTestUserAccount.of(username, password,
                    email);
                OptimisticTestUserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeService.create(postLikeDTO);
            } catch (Exception exception) {
                conflictCount.getAndIncrement();
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        OptimisticTestPost post = postRepository.findById(savedTestPost.getId()).orElseThrow();
        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount - conflictCount.get());

    }
}
