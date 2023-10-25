package me.honki12345.hoonlog.concurrency;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_PASSWORD;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import me.honki12345.hoonlog.concurrency.domain.TestPost;
import me.honki12345.hoonlog.concurrency.domain.TestUserAccount;
import me.honki12345.hoonlog.concurrency.repository.TestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.repository.TestPostRepository;
import me.honki12345.hoonlog.concurrency.repository.TestUserAccountRepository;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.util.ConcurrencyTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostLike Pessimistic lock Test")
public class PostLikeServiceConcurrencyTest extends ConcurrencyTestSupport {

    private final int threadCount = 300;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @Autowired
    TestUserAccountRepository userAccountRepository;
    @Autowired
    TestPostRepository postRepository;
    @Autowired
    TestPostLikeRepository postLikeRepository;
    @Autowired
    me.honki12345.hoonlog.concurrency.service.PostLikeServiceConcurrencyTest postLikeService;

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


    @DisplayName("게시글에 좋아요 요청이 동시에 이루어질 경우, 게시글의 좋아요 카운트에 비관적 락을 걸었다면, 데이터 무결성을 보장한다")
    @Test
    void givenMultiThreadingRequest_whenPessimisticLockOnPostLike_thenReturnsLikeCountSameAsMultiThread() throws InterruptedException {
        // given
        TestUserAccount testUserAccount = TestUserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com");
        userAccountRepository.save(testUserAccount);
        TestPost testPost = TestPost.of(null, testUserAccount, "title", "content");
        TestPost savedTestPost = postRepository.save(testPost);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                TestUserAccount userAccount = TestUserAccount.of(username, password,
                    email);
                TestUserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeService.createOnPessimisticLock(postLikeDTO);
            } catch (Exception exception) {
                System.out.println("exception: " + exception.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        TestPost post = postRepository.findById(savedTestPost.getId()).orElseThrow();
        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount);

    }

    @DisplayName("게시글에 좋아요 요청이 동시에 이루어질 경우, 게시글의 좋아요 카운트에 낙관적 락을 걸었다면, 데이터 무결성을 보장한다")
    @Test
    void givenMultiThreadingRequest_whenOptimisticLockOnPostLike_thenReturnsLikeCountSameAsMultiThread2() throws InterruptedException {
        // given
        TestUserAccount testUserAccount = TestUserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com");
        userAccountRepository.save(testUserAccount);
        TestPost testPost = TestPost.of(null, testUserAccount, "title", "content");
        TestPost savedTestPost = postRepository.save(testPost);
        AtomicInteger conflictCount = new AtomicInteger();

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                TestUserAccount userAccount = TestUserAccount.of(username, password,
                    email);
                TestUserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeService.createOnOptimisticLock(postLikeDTO);
            } catch (Exception exception) {
                conflictCount.getAndIncrement();
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        TestPost post = postRepository.findById(savedTestPost.getId()).orElseThrow();
        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount - conflictCount.get());

    }

}
