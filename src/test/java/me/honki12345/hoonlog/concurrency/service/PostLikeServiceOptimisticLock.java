package me.honki12345.hoonlog.concurrency.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.concurrency.repository.PostRepositoryOptimisticLock;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostLike;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.DuplicatePostLikeException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeServiceOptimisticLock {

    private final PostLikeRepository postLikeRepository;
    private final PostRepositoryOptimisticLock postRepository;
    private final UserAccountRepository userAccountRepository;

    public void createOnOptimisticLock(PostLikeDTO postLikeDTO) {
        UserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postRepository.findByPostOnOptimisticLock(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
            postLikeDTO.userId())) {
            throw new DuplicatePostLikeException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        PostLike postLike = PostLike.createEmptyPostLike();
        userAccount.addPostLike(postLike);
        post.addPostLike(postLike);
        postLikeRepository.save(postLike);
    }
}
