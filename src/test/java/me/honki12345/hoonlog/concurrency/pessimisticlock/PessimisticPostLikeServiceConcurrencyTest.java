package me.honki12345.hoonlog.concurrency.pessimisticlock;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_PASSWORD;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestUserAccountRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.service.PessimisticTestPostLikeService;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.util.ConcurrencyTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostLike Pessimistic lock Test")
public class PessimisticPostLikeServiceConcurrencyTest extends ConcurrencyTestSupport {

    private final int threadCount = 300;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @Autowired
    PessimisticTestUserAccountRepository userAccountRepository;
    @Autowired
    PessimisticTestPostRepository postRepository;
    @Autowired
    PessimisticTestPostLikeRepository postLikeRepository;
    @Autowired
    PessimisticTestPostLikeService postLikeService;

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
        PessimisticTestUserAccount testUserAccount = PessimisticTestUserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com");
        userAccountRepository.save(testUserAccount);
        PessimisticTestPost testPost = PessimisticTestPost.of(null, testUserAccount, "title", "content");
        PessimisticTestPost savedTestPost = postRepository.save(testPost);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                PessimisticTestUserAccount userAccount = PessimisticTestUserAccount.of(username, password,
                    email);
                PessimisticTestUserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeService.create(postLikeDTO);
            } catch (Exception exception) {
                System.out.println("exception: " + exception.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        PessimisticTestPost post = postRepository.findById(savedTestPost.getId()).orElseThrow();
//        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount);

    }

}
