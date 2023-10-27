package me.honki12345.hoonlog.util;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.NotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class PostBuilder {

    public static final String TEST_POST_TITLE = "title";
    public static final String TEST_POST_CONTENT = "content";
    public static final String TEST_UPDATED_POST_TITLE = "updatedTitle";
    public static final String TEST_UPDATED_POST_CONTENT = "updatedContent";

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;

    public void deleteAllInBatch() {
        this.postLikeRepository.deleteAllInBatch();
        this.postImageRepository.deleteAllInBatch();
        this.postRepository.deleteAllInBatch();
    }

    public Post createPostByTestUser() {
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.saveAndFlush(
                postRequest.toDTO().toEntity().addUserAccount(userAccount)))
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    public Post createPostByTestUser(String title, String content) {
        PostRequest postRequest = PostRequest.of(title, content);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
                postRequest.toDTO().toEntity().addUserAccount(userAccount)))
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    public Post createPostWithImageFileByTestUser(PostImage postImage) {
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);

        return optionalUserAccount.map(userAccount -> postRepository.saveAndFlush(
                postRequest.toDTO().toEntity().addUserAccount(userAccount)).addPostImage(postImage))
            .orElse(null);
    }
}
