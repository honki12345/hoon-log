package me.honki12345.hoonlog.util;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.service.AuthService;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TokenBuilder {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountBuilder userAccountBuilder;

    public void deleteAllInBatch() {
        this.refreshTokenRepository.deleteAllInBatch();
    }

    public TokenDTO createTokens(UserAccountDTO userAccountDTO) {
        return authService.createTokens(userAccountDTO);
    }

    public TokenDTO createTokensAfterSavingTestUser() {
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        return authService.createTokens(userAccountDTO);
    }
}
