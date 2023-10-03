package me.honki12345.hoonlog.service;

import java.util.Set;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.exception.security.LogoutErrorException;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AuthService 애플리케이션 통합테스트")
@ActiveProfiles("test")
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    JwtTokenizer jwtTokenizer;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
    }

    @DisplayName("[로그인/성공]유저 정보를 입력시, 토큰생성을 성공한다.")
    @Test
    void givenUserInfo_whenLogin_thenReturnsTokens() {
        // given
        UserAccountDTO userAccountDTO = createUserAccountDTO();

        // when
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        String accessToken = tokenDTO.accessToken();
        String refreshToken = tokenDTO.refreshToken();
        Long userIdFromAccessToken = jwtTokenizer.getUserIdFromAccessToken(accessToken);
        Long userIdFromRefreshToken = jwtTokenizer.getUserIdFromRefreshToken(refreshToken);

        // then
        assertThat(refreshTokenRepository.existsByToken(refreshToken)).isTrue();
        assertThat(userIdFromAccessToken).isEqualTo(1L);
        assertThat(userIdFromRefreshToken).isEqualTo(1L);
    }

    @DisplayName("[로그아웃/성공]저장된 리프레쉬 토큰을 입력하고, 로그아웃을 하면, 토큰을 삭제한다.")
    @Test
    void givenSavedRefreshToken_whenLogout_thenDeletingSavedRefreshToken() {
        // given
        UserAccountDTO userAccountDTO = createUserAccountDTO();
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        // when
        authService.deleteRefreshToken(tokenDTO.refreshToken());

        // then
        assertThat(refreshTokenRepository.existsByToken(tokenDTO.refreshToken())).isFalse();
    }

    @DisplayName("[로그아웃/실패]저장되지 않은 리프레쉬 토큰을 입력하고, 로그아웃을 하면, 예외를 던진다.")
    @Test
    void givenNotSavedRefreshToken_whenLogout_thenThrowsException() {
        // given // when // then
        assertThatThrownBy(() -> authService.deleteRefreshToken("wrongRefreshToken")).isInstanceOf(
            LogoutErrorException.class);
    }

    @DisplayName("[재발급/성공]저장된 리프레쉬 토큰을 입력하고, 토큰 재발급을 하면, 새 엑세스 토큰을 반환한다.")
    @Test
    void givenSavedRefreshToken_whenRefreshingToken_thenReturnsNewAccessToken() {
        // given
        UserAccount userAccount = userAccountRepository.save(
            UserAccount.of("fpg123", "12345678", null, Profile.of("name", null)));
        TokenDTO tokenDTO = authService.createTokens(UserAccountDTO.from(userAccount));

        // when
        TokenDTO refreshTokenDTO = authService.refreshAccessToken(tokenDTO.refreshToken());
        String accessToken = refreshTokenDTO.accessToken();

        // then
        assertThat(jwtTokenizer.getUserIdFromAccessToken(accessToken)).isEqualTo(
            userAccount.getId());
    }

    private static UserAccountDTO createUserAccountDTO() {
        Set<Role> roles = Set.of(Role.of("ROLE_USER"));
        UserAccountDTO userAccountDTO = new UserAccountDTO(1L, "username", "password", null, null,
            null, roles);
        return userAccountDTO;
    }
}