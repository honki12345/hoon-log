package me.honki12345.hoonlog.concurrency.pessimisticlock.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestPostLikeRepository;
import me.honki12345.hoonlog.concurrency.pessimisticlock.repository.PessimisticTestUserAccountRepository;
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
public class PessimisticTestPostLikeService {

    private final PessimisticTestPostLikeRepository postLikeRepository;
    private final PessimisticTestPostRepository postRepository;
    private final PessimisticTestUserAccountRepository userAccountRepository;

    public void create(PostLikeDTO postLikeDTO) {
        PessimisticTestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        PessimisticTestPost post = postRepository.findByPostIdWithAll(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
            postLikeDTO.userId())) {
            throw new DuplicatePostLikeException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        PessimisticTestPostLike postLike = PessimisticTestPostLike.createEmptyPostLike();
        userAccount.addPostLike(postLike);
        post.addPostLike(postLike);
        postLikeRepository.save(postLike);
    }

    public void delete(PostLikeDTO postLikeDTO) {
        PessimisticTestUserAccount userAccount = userAccountRepository.findByIdWithPostLike(
                postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        PessimisticTestPost post = postRepository.findByPostIdWithAll(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        PessimisticTestPostLike postLike = postLikeRepository.findByPost_IdAndUserAccount_Id(
                postLikeDTO.postId(),
                postLikeDTO.userId())
            .orElseThrow(() -> new PostLikeNotFoundException(ErrorCode.POST_LIKE_NOT_FOUND));
        userAccount.deletePostLike(postLike);
        post.deletePostLike(postLike);
        postLikeRepository.delete(postLike);
    }
}
