package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostCommentNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostRepository postRepository;

    public PostCommentDTO addPostComment(PostCommentDTO postCommentDTO,
        Long postId, UserAccountDTO userAccountDTO) {
        PostComment postComment = postCommentDTO.toEntity();
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        UserAccount userAccount = userAccountRepository.findById(userAccountDTO.id())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        postComment = postComment.addPost(post).addUserAccount(userAccount);
        return PostCommentDTO.from(postCommentRepository.save(postComment));
    }

    public PostCommentDTO modifyComment(PostCommentDTO postCommentDTO, Long commentId,
        UserAccountDTO dto) {
        PostComment postComment = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new PostCommentNotFoundException(
                ErrorCode.COMMENT_NOT_FOUND));
        if (!postComment.getUserAccount().getId().equals(dto.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        postComment.update(postCommentDTO.content());
        return PostCommentDTO.from(postComment);
    }
}
