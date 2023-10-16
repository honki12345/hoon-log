package me.honki12345.hoonlog.util;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class PostCommentBuilder {

    public static final String TEST_COMMENT_CONTENT = "commentContent";

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public void deleteAllInBatch() {
        this.postCommentRepository.deleteAllInBatch();
        this.postRepository.deleteAllInBatch();
        this.userAccountRepository.deleteAllInBatch();
    }

    public PostComment createCommentByTestUser(Long postId) {
        PostCommentRequest postCommentRequest = PostCommentRequest.of(
            TEST_COMMENT_CONTENT);
        PostComment postComment = postCommentRequest.toDTO().toEntity();

        UserAccount userAccount = userAccountRepository.findByUsername(TEST_USERNAME)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));

        postComment.addPost(post);
        postComment.addUserAccount(userAccount);
        return postCommentRepository.save(postComment);
    }
}
