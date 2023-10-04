package me.honki12345.hoonlog.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TestUtil {

    public static final String TEST_USERNAME = "fpg123";
    public static final String TEST_PASSWORD = "12345678";
    public static final String TEST_POST_TITLE = "title";
    public static final String TEST_POST_CONTENT = "content";
    public static final String TEST_COMMENT_CONTENT = "commentContent";

    private final AuthService authService;
    private final UserAccountService userAccountService;

    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostCommentRepository postCommentRepository;


    public void deleteAllInBatchInAllRepository() {
        this.postCommentRepository.deleteAllInBatch();
        this.postRepository.deleteAllInBatch();
        this.refreshTokenRepository.deleteAllInBatch();
        this.userAccountRepository.deleteAllInBatch();
    }

    public TokenDTO createTokensAfterSavingTestUser() {
        UserAccountDTO userAccountDTO = saveTestUser();
        return authService.createTokens(userAccountDTO);
    }

    public TokenDTO createTokensAfterSavingTestUser(String username, String password) {
        UserAccountDTO userAccountDTO = saveTestUser(username, password);
        return authService.createTokens(userAccountDTO);
    }

    public UserAccountDTO saveTestUser() {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(TEST_USERNAME, TEST_PASSWORD, "fpg123@mail.com", profileDTO);
    }


    public UserAccountDTO saveTestUser(String username, String password) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, "fpg123@mail.com", profileDTO);
    }

    public UserAccountDTO saveTestUser(String username, String password, String email) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, email, profileDTO);
    }


    public UserAccountDTO saveTestUser(String username, String password, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        return userAccountService.saveUserAccount(request.toDTO());
    }

    public Post createPostWithTestUser() {
        PostRequest postRequest = new PostRequest(TEST_POST_TITLE, TEST_POST_CONTENT);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.saveAndFlush(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }

    public Post createPostWithTestUser(String title, String content) {
        PostRequest postRequest = new PostRequest(title, content);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }

    public PostComment createCommentWithTestUser(Long postId) {
        PostCommentRequest postCommentRequest = PostCommentRequest.of(
            TestUtil.TEST_COMMENT_CONTENT);
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
