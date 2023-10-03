package me.honki12345.hoonlog.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDTO::from);
    }

    public PostDTO addPost(@Valid PostRequest request,
        UserAccountPrincipal userAccountPrincipal) {
        UserAccount userAccount = userAccountRepository.findById(userAccountPrincipal.userId())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = request.toEntityWithUserAccount(userAccount);
        return PostDTO.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public PostDTO searchPost(Long postId) {
        return PostDTO.from(postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND)));
    }

    public PostDTO updatePost(Long postId, UserAccountPrincipal userAccountPrincipal,
        @Valid PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        if (!post.getUserAccount().getUsername().equals(userAccountPrincipal.username())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        post.update(postRequest);
        return PostDTO.from(post);
    }

    public void deletePost(Long postId, UserAccountPrincipal userAccountPrincipal) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        if (!post.getUserAccount().getUsername().equals(userAccountPrincipal.username())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        postRepository.deleteById(postId);
    }
}
