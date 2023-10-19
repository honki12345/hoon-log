package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostLike;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.DuplicatePostLikeException;
import me.honki12345.hoonlog.error.exception.domain.PostLikeNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    public void create(PostLikeDTO postLikeDTO) {
        UserAccount userAccount = userAccountRepository.findByIdWithPostLike(postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postRepository.findByPostIdOnPessimisticLock(postLikeDTO.postId())
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

    public void delete(PostLikeDTO postLikeDTO) {
        UserAccount userAccount = userAccountRepository.findByIdWithPostLike(postLikeDTO.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postRepository.findByPostIdOnPessimisticLock(postLikeDTO.postId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        PostLike postLike = postLikeRepository.findByPost_IdAndUserAccount_Id(postLikeDTO.postId(),
                postLikeDTO.userId())
            .orElseThrow(() -> new PostLikeNotFoundException(ErrorCode.POST_LIKE_NOT_FOUND));
        userAccount.deletePostLike(postLike);
        post.deletePostLike(postLike);
        postLikeRepository.delete(postLike);
    }
}
