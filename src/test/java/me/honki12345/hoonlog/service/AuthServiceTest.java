package me.honki12345.hoonlog.service;

import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_PASSWORD;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import me.honki12345.hoonlog.config.Initializer;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.exception.domain.TokenNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.error.exception.security.LogoutErrorException;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.RoleRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuthService 애플리케이션 통합테스트")
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private UserAccountBuilder userAccountBuilder;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        userAccountBuilder.deleteAllInBatch();
    }

    @DisplayName("[로그인/성공]유저 정보를 입력시, 토큰생성을 성공한다.")
    @Test
    void givenUserInfo_whenLogin_thenReturnsTokens() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();

        // when
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        String accessToken = tokenDTO.accessToken();
        String refreshToken = tokenDTO.refreshToken();
        Long userIdFromAccessToken = jwtTokenizer.getUserIdFromAccessToken(accessToken);
        Long userIdFromRefreshToken = jwtTokenizer.getUserIdFromRefreshToken(refreshToken);

        // then
        assertThat(refreshTokenRepository.existsByToken(refreshToken)).isTrue();
        assertThat(userIdFromAccessToken).isEqualTo(userAccountDTO.id());
        assertThat(userIdFromRefreshToken).isEqualTo(userAccountDTO.id());
    }

    @DisplayName("[로그인/성공]유저 정보를 입력시, 토큰생성을 성공한다. (ver2)")
    @Test
    void givenUserInfo_whenLogin_thenReturnsTokens_ver2() {
        // given
        String roleName = "ROLE_USER2";
        Role role2 = roleRepository.save(Role.of(roleName));
        roleRepository.save(role2);

        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser(TEST_USERNAME, TEST_PASSWORD,
            "test@mail.com",
            new ProfileDTO("name", null),
            Set.of(role2));

        // when
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        String accessToken = tokenDTO.accessToken();
        String refreshToken = tokenDTO.refreshToken();
        Long userIdFromAccessToken = jwtTokenizer.getUserIdFromAccessToken(accessToken);
        Long userIdFromRefreshToken = jwtTokenizer.getUserIdFromRefreshToken(refreshToken);
        List<String> rolesFromAccessToken = jwtTokenizer.getRolesFromAccessToken(accessToken);

        // then
        assertThat(refreshTokenRepository.existsByToken(refreshToken)).isTrue();
        assertThat(userIdFromAccessToken).isEqualTo(userAccountDTO.id());
        assertThat(userIdFromRefreshToken).isEqualTo(userAccountDTO.id());
        assertThat(rolesFromAccessToken).contains(Initializer.DEFAULT_ROLE_NAME, roleName);
    }


    @DisplayName("[로그아웃/성공]저장된 리프레쉬 토큰을 입력하고, 로그아웃을 하면, 토큰을 삭제한다.")
    @Test
    void givenSavedRefreshToken_whenLogout_thenDeletingSavedRefreshToken() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
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

    @DisplayName("[재발급/실패]유효하지 않은 회원의 리프레쉬 토큰으로, 토큰 재발급을 하면, 예외를 던진다.")
    @Test
    void givenRefreshTokenOfInvalidUserInfo_whenRefreshingToken_thenThrowsException() {
        // given
        long wrongUserId = 999L;
        UserAccount unsavedUserAccount = UserAccount.of(wrongUserId, "fpg123", "12345678", null,
            Profile.of("name", null));
        TokenDTO tokenDTO = authService.createTokens(UserAccountDTO.from(unsavedUserAccount));

        // when // then
        assertThatThrownBy(() -> authService.refreshAccessToken(tokenDTO.refreshToken()))
            .isInstanceOf(UserAccountNotFoundException.class);
    }

    @DisplayName("[재발급/실패]유효하지 않은 리프레쉬 토큰으로, 토큰 재발급을 하면, 예외를 던진다.")
    @Test
    void givenInvalidRefreshToken_whenRefreshingToken_thenThrowsException() {
        // given
        TokenDTO tokenDTO = TokenDTO.of("wrongAccessToken", "wrongRefreshToken");

        // when // then
        assertThatThrownBy(() -> authService.refreshAccessToken(tokenDTO.refreshToken()))
            .isInstanceOf(TokenNotFoundException.class);
    }
}