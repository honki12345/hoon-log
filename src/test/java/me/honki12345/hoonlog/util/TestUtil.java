package me.honki12345.hoonlog.util;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
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

    public TokenDTO createTokensAfterSaving(String username, String password) {
        UserAccountDTO userAccountDTO = saveOneUserAccount(username, password);
        return authService.createTokens(userAccountDTO);
    }

    public UserAccountDTO saveOneUserAccount(String username, String password) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveOneUserAccount(username, password, "fpg123@mail.com", profileDTO);
    }


    public UserAccountDTO saveOneUserAccount(String username, String password, String email) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveOneUserAccount(username, password, email, profileDTO);
    }


    public UserAccountDTO saveOneUserAccount(String username, String password, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        return userAccountService.saveUserAccount(request);
    }
}
