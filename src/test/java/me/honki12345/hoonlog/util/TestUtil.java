package me.honki12345.hoonlog.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TestUtil {

    public static final String TEST_USERNAME = "fpg123";
    public static final String TEST_WRONG_USERNAME = "wrongFpg123";
    public static final String TEST_PASSWORD = "12345678";
    public static final String TEST_TITLE= "title";
    public static final String TEST_CONTENT= "content";

    private final AuthService authService;
    private final UserAccountService userAccountService;
    private final PostService postService;

    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;


    public void deleteAllInBatchInAllRepository() {
        this.postRepository.deleteAllInBatch();
        this.refreshTokenRepository.deleteAllInBatch();
        this.userAccountRepository.deleteAllInBatch();
    }

    public TokenDTO createTokensAfterSaving() {
        UserAccountDTO userAccountDTO = saveTestUser();
        return authService.createTokens(userAccountDTO);
    }

    public TokenDTO createTokensAfterSaving(String username, String password) {
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
        return userAccountService.saveUserAccount(request);
    }

    public Post createPostWithTestUser(String title, String content) {
        PostRequest postRequest = new PostRequest(title, content);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
            postRequest.toEntityWithUserAccount(userAccount))).orElse(null);
    }
}
