package me.honki12345.hoonlog.concurrency.optimisticlock.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPost;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.optimisticlock.repository.OptimisticTestUserAccountRepository;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.DuplicatePostLikeException;
import me.honki12345.hoonlog.error.exception.domain.PostLikeNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OptimisticTestPostLikeService {

    private final OptimisticTestPostLikeRepository postLikeRepository;
    private final OptimisticTestPostRepository postRepository;
    private final OptimisticTestUserAccountRepository userAccountRepository;

    public void create(PostLikeDTO postLikeDTO) {
        OptimisticTestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        OptimisticTestPost post = postRepository.findByPostIdWithAll(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
            postLikeDTO.userId())) {
            throw new DuplicatePostLikeException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        OptimisticTestPostLike postLike = OptimisticTestPostLike.createEmptyPostLike();
        userAccount.addPostLike(postLike);
        post.addPostLike(postLike);
        postLikeRepository.save(postLike);
    }

    public void delete(PostLikeDTO postLikeDTO) {
        OptimisticTestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        OptimisticTestPost post = postRepository.findByPostIdWithAll(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        OptimisticTestPostLike postLike = postLikeRepository.findByPost_IdAndUserAccount_Id(
                postLikeDTO.postId(),
                postLikeDTO.userId())
            .orElseThrow(() -> new PostLikeNotFoundException(ErrorCode.POST_LIKE_NOT_FOUND));
        userAccount.deletePostLike(postLike);
        post.deletePostLike(postLike);
        postLikeRepository.delete(postLike);
    }
}
