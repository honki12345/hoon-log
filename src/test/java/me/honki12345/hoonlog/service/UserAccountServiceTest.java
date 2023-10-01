package me.honki12345.hoonlog.service;

import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.error.exception.LoginErrorException;
import me.honki12345.hoonlog.error.exception.UserAccountNotFoundException;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserAccountService 애플리케이션 통합테스트")
@ActiveProfiles("test")
@SpringBootTest
class UserAccountServiceTest {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAllInBatch();
    }

    @DisplayName("[가입/성공]유저 정보를 입력하면, 회원가입 시, id와 encoded 비밀번호와 가입일자 정보가 포함된 유저 객체를 생성한다.")
    @Test
    void givenSignUpRequest_whenSignUp_thenContainingIdAndEncodedPWDAndCreatedAt() {
        // given
        String userPassword = "12345678";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", userPassword, "fpg123@mail.com", profileDTO);

        // when
        UserAccountDTO actual = userAccountService.saveUserAccount(request);

        // then
        assertThat(actual)
            .hasFieldOrProperty("id")
            .hasFieldOrProperty("createdAt");
        assertThat(passwordEncoder.matches(userPassword, actual.userPassword()));
    }

    @DisplayName("[가입/실패]중복된 유저 아이디가 있으면, 회원가입 시, 예외를 발생한다.")
    @Test
    void givenSignUpRequestWithDuplicateUserId_whenSignUp_thenThrowingException() {
        // given
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", profileDTO);
        userAccountService.saveUserAccount(request);

        // when // then
        assertThatThrownBy(() -> userAccountService.saveUserAccount(request)).isInstanceOf(
            DuplicateUserAccountException.class);
    }

    @DisplayName("[조회/실패]존재하지 않는 유저 아이디로, 회원조회 시, 예외를 발생한다.")
    @Test
    void givenNotFoundUserId_whenFindUserAccount_thenThrowingException() {
        // given // when // then
        assertThatThrownBy(
            () -> userAccountService.findUserAccountByUsername("fpg123")).isInstanceOf(
            UserAccountNotFoundException.class);
    }

    @DisplayName("[조회/성공]주어진 아이디와 비밀번호가, 저장된 값과 일치하면, 유저엔티티를 반환한다")
    @Test
    void givenLoginInfo_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        String password = "12345678";
        UserAccountDTO userAccountDTO = saveOneUserAccount("fpg123", password);
        LoginRequest loginRequest = new LoginRequest(userAccountDTO.username(), password);

        // when
        UserAccountDTO findUserAccountDTO = userAccountService.findUserAccountAfterCheckingPassword(
            loginRequest);

        // then
        assertThat(findUserAccountDTO).hasFieldOrPropertyWithValue("id", userAccountDTO.id())
            .hasFieldOrPropertyWithValue("username", userAccountDTO.username())
            .hasFieldOrPropertyWithValue("userPassword", userAccountDTO.userPassword());
    }

    @DisplayName("[조회/실패]주어진 아이디가, 저장된 값과 일치하면, 예외를 반환한다")
    @Test
    void givenWrongUsername_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        String password = "12345678";
        saveOneUserAccount("fpg123", password);
        LoginRequest loginRequest = new LoginRequest("wrongUserId", password);

        // when
        assertThatThrownBy(
            () -> userAccountService.findUserAccountAfterCheckingPassword(loginRequest))
            .isInstanceOf(LoginErrorException.class);
    }

    @DisplayName("[조회/실패]주어진 비밀번호가, 저장된 값과 일치하면, 예외를 반환한다")
    @Test
    void givenWrongPassword_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        String username = "fpg123";
        saveOneUserAccount(username, "12345678");
        LoginRequest loginRequest = new LoginRequest(username, "wrongPassword");

        // when
        assertThatThrownBy(
            () -> userAccountService.findUserAccountAfterCheckingPassword(loginRequest))
            .isInstanceOf(LoginErrorException.class);
    }

    @DisplayName("[수정/성공]수정정보를 입력하면, 회원수정 시, 수정된 회원객체 DTO를 반환한다")
    @Test
    void givenModifyingInfo_whenModifyingUserAccount_thenReturnsModifiedUserAccountDTO() {
        // given
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", profileDTO);
        userAccountService.saveUserAccount(request);

        String changedBlogName = "changedBlogName";
        String changedBlogBio = "changedBlogBio";
        UserAccountModifyRequest modifyRequest = new UserAccountModifyRequest(
            new ProfileDTO(changedBlogName, changedBlogBio)
        );

        // when
        UserAccountDTO userAccountDTO = userAccountService.modifyUserAccount("fpg123",
            modifyRequest);
        ProfileDTO changedProfileDTO = userAccountDTO.profileDTO();

        // then
        assertThat(changedProfileDTO)
            .hasFieldOrPropertyWithValue("blogName", changedBlogName)
            .hasFieldOrPropertyWithValue("blogShortBio", changedBlogBio);
    }

    @DisplayName("[수정/실패]존재하지 않는 userId를 입력하면, 회원수정 시, 예외를 발생한다")
    @Test
    void givenModifyingInfoWithWrongUserId_whenModifyingUserAccount_thenThrowsException() {
        // given
        UserAccountModifyRequest modifyRequest = new UserAccountModifyRequest(
            new ProfileDTO("changedBlogName", "changedBlogBio")
        );

        // when // then
        assertThatThrownBy(() -> userAccountService.modifyUserAccount("wrongUserId", modifyRequest))
            .isInstanceOf(UserAccountNotFoundException.class);
    }

    private UserAccountDTO saveOneUserAccount(String username, String password) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveOneUserAccount(username, password, "fpg123@mail.com", profileDTO);
    }


    private UserAccountDTO saveOneUserAccount(String username, String password, String email) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveOneUserAccount(username, password, email, profileDTO);
    }


    private UserAccountDTO saveOneUserAccount(String username, String password, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        return userAccountService.saveUserAccount(request);
    }

}