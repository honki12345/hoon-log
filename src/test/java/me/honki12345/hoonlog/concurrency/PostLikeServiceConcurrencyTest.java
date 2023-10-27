package me.honki12345.hoonlog.concurrency;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_PASSWORD;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import me.honki12345.hoonlog.concurrency.repository.PostRepositoryOptimisticLock;
import me.honki12345.hoonlog.concurrency.service.PostLikeServiceOptimisticLock;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.PostLikeService;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostLike Pessimistic lock Test")
public class PostLikeServiceConcurrencyTest extends IntegrationTestSupport {

    private final int threadCount = 300;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PostRepository postRepositoryPessimisticLock;
    @Autowired
    PostRepositoryOptimisticLock postRepositoryOptimisticLock;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    PostLikeService postLikeServicePessimisticLock;
    @Autowired
    PostLikeServiceOptimisticLock postLikeServiceOptimisticLock;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(threadCount);
        countDownLatch = new CountDownLatch(threadCount);
    }

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postRepositoryPessimisticLock.deleteAllInBatch();
        userAccountRepository.deleteAllInBatch();
    }


    @DisplayName("게시글에 좋아요 요청이 동시에 이루어질 경우, 게시글의 좋아요 카운트에 비관적 락을 걸었다면, 데이터 무결성을 보장한다")
    @Test
    void givenMultiThreadingRequest_whenPessimisticLockOnPostLike_thenReturnsLikeCountSameAsMultiThread()
        throws InterruptedException {
        // given
        UserAccount testUserAccount = UserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com", Profile.of("blogName", "blogBio"));
        userAccountRepository.save(testUserAccount);
        Post testPost = Post.of(null, testUserAccount, "title", "content");
        Post savedTestPost = postRepositoryPessimisticLock.save(testPost);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                UserAccount userAccount = UserAccount.of(username, password,
                    email, Profile.of("name", "bio"));
                UserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeServicePessimisticLock.create(postLikeDTO);
            } catch (Exception exception) {
                System.out.println("exception: " + exception.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        Post post = postRepositoryPessimisticLock.findById(savedTestPost.getId()).orElseThrow();
        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount);

    }

    @DisplayName("게시글에 좋아요 요청이 동시에 이루어질 경우, 게시글의 좋아요 카운트에 낙관적 락을 걸었다면, 데이터 무결성을 보장한다")
    @Test
    void givenMultiThreadingRequest_whenOptimisticLockOnPostLike_thenReturnsLikeCountSameAsMultiThread()
        throws InterruptedException {
        // given
        UserAccount testUserAccount = UserAccount.of(TEST_USERNAME, TEST_PASSWORD,
            "test@email.com", Profile.of("name", "bio"));
        userAccountRepository.save(testUserAccount);
        Post testPost = Post.of(null, testUserAccount, "title", "content");
        Post savedTestPost = postRepositoryOptimisticLock.save(testPost);
        AtomicInteger conflictCount = new AtomicInteger();

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
            try {
                String username = "test" + e;
                String password = "pwd" + e;
                String email = "test" + e + "@mail.com";
                UserAccount userAccount = UserAccount.of(username, password,
                    email, Profile.of("name", "bio"));
                UserAccount savedTestUserAccount = userAccountRepository.save(userAccount);
                PostLikeDTO postLikeDTO = new PostLikeDTO(savedTestPost.getId(),
                    savedTestUserAccount.getId());
                postLikeServiceOptimisticLock.createOnOptimisticLock(postLikeDTO);
            } catch (Exception exception) {
                conflictCount.getAndIncrement();
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        // then
        Post post = postRepositoryOptimisticLock.findById(savedTestPost.getId()).orElseThrow();
        System.out.println("post.getLikeCount() = " + post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(threadCount - conflictCount.get());

    }

}
