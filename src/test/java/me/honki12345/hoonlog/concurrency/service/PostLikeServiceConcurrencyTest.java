package me.honki12345.hoonlog.concurrency.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.concurrency.repository.TestPostRepository;
import me.honki12345.hoonlog.concurrency.domain.TestPost;
import me.honki12345.hoonlog.concurrency.domain.TestPostLike;
import me.honki12345.hoonlog.concurrency.domain.TestUserAccount;
import me.honki12345.hoonlog.concurrency.repository.TestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.repository.TestUserAccountRepository;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.DuplicatePostLikeException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeServiceConcurrencyTest {

    private final TestPostLikeRepository postLikeRepository;
    private final TestPostRepository postRepository;
    private final TestUserAccountRepository userAccountRepository;

    public void createOnPessimisticLock(PostLikeDTO postLikeDTO) {
        TestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        TestPost post = postRepository.findByPostIdWithAll(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
            postLikeDTO.userId())) {
            throw new DuplicatePostLikeException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        TestPostLike postLike = TestPostLike.createEmptyPostLike();
        userAccount.addPostLike(postLike);
        post.addPostLike(postLike);
        postLikeRepository.save(postLike);
    }

    public void createOnOptimisticLock(PostLikeDTO postLikeDTO) {
        TestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        TestPost post = postRepository.findByPostIdWithAll2(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
            postLikeDTO.userId())) {
            throw new DuplicatePostLikeException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        TestPostLike postLike = TestPostLike.createEmptyPostLike();
        userAccount.addPostLike(postLike);
        post.addPostLike(postLike);
        postLikeRepository.save(postLike);
    }
}
