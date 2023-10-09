package me.honki12345.hoonlog.service;

import me.honki12345.hoonlog.config.Initializer;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.error.exception.domain.DuplicateUserAccountException;
import me.honki12345.hoonlog.error.exception.domain.RoleNotFoundException;
import me.honki12345.hoonlog.error.exception.security.LoginErrorException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.repository.RoleRepository;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static me.honki12345.hoonlog.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("UserAccountService 애플리케이션 통합테스트")
@ActiveProfiles("test")
@Import({TestUtils.class})
@SpringBootTest
class UserAccountServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
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
        UserAccountDTO actual = userAccountService.saveUserAccount(request.toDTO());

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
        userAccountService.saveUserAccount(request.toDTO());

        // when // then
        assertThatThrownBy(() -> userAccountService.saveUserAccount(request.toDTO())).isInstanceOf(
            DuplicateUserAccountException.class);
    }

    @Transactional
    @DisplayName("[가입/실패]SetUp 된 Role 객체가 없으시, 회원가입 하면, 예외를 발생한다.")
    @Test
    void givenNotSavedRole_whenSignUp_thenThrowingException() {
        // given
        roleRepository.deleteByName(Initializer.DEFAULT_ROLE_NAME);
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", profileDTO);

        // when // then
        assertThatThrownBy(() -> userAccountService.saveUserAccount(request.toDTO())).isInstanceOf(
            RoleNotFoundException.class);
    }


    @DisplayName("[조회/성공]주어진 아이디와 비밀번호가, 저장된 값과 일치하면, 유저엔티티를 반환한다")
    @Test
    void givenLoginInfo_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        LoginRequest loginRequest = new LoginRequest(userAccountDTO.username(), TEST_PASSWORD);

        // when
        UserAccountDTO findUserAccountDTO = userAccountService.findUserAccountAfterCheckingPassword(
            loginRequest.toDTO());

        // then
        assertThat(findUserAccountDTO).hasFieldOrPropertyWithValue("id", userAccountDTO.id())
            .hasFieldOrPropertyWithValue("username", userAccountDTO.username())
            .hasFieldOrPropertyWithValue("userPassword", userAccountDTO.userPassword());
    }

    @DisplayName("[조회/실패]존재하지 않는 userId로, 회원조회 시, 예외를 발생한다.")
    @Test
    void givenNotFoundUserId_whenFindUserAccount_thenThrowingException() {
        // given // when // then
        assertThatThrownBy(
            () -> userAccountService.findUserAccountByUserId(999L)).isInstanceOf(
            UserAccountNotFoundException.class);
    }

    @DisplayName("[조회/실패]존재하지 않는 username 으로, 회원조회 시, 예외를 발생한다.")
    @Test
    void givenNotFoundUsername_whenFindUserAccount_thenThrowingException() {
        // given // when // then
        assertThatThrownBy(
            () -> userAccountService.findUserAccountByUsername("fpg123")).isInstanceOf(
            UserAccountNotFoundException.class);
    }


    @DisplayName("[조회/실패]존재하지 않는 유저 아이디로, 비밀번호 확인 시, 예외를 반환한다")
    @Test
    void givenWrongUsername_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        testUtils.saveTestUser();
        LoginRequest loginRequest = new LoginRequest("wrongUserId", TEST_PASSWORD);

        // when
        assertThatThrownBy(
            () -> userAccountService.findUserAccountAfterCheckingPassword(loginRequest.toDTO()))
            .isInstanceOf(LoginErrorException.class);
    }

    @DisplayName("[조회/실패]주어진 비밀번호가, 저장된 값과 일치하면, 예외를 반환한다")
    @Test
    void givenWrongPassword_whenFindEntityCheckingPassword_thenReturnsEntity() {
        // given
        testUtils.saveTestUser();
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, "wrongPassword");

        // when
        assertThatThrownBy(
            () -> userAccountService.findUserAccountAfterCheckingPassword(loginRequest.toDTO()))
            .isInstanceOf(LoginErrorException.class);
    }

    @DisplayName("[수정/성공]수정정보를 입력하면, 회원수정 시, 수정된 회원객체 DTO를 반환한다")
    @Test
    void givenModifyingInfo_whenModifyingUserAccount_thenReturnsModifiedUserAccountDTO() {
        // given
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", profileDTO);
        userAccountService.saveUserAccount(request.toDTO());

        String changedBlogName = "changedBlogName";
        String changedBlogBio = "changedBlogBio";
        UserAccountModifyRequest modifyRequest = new UserAccountModifyRequest(
            new ProfileDTO(changedBlogName, changedBlogBio)
        );

        // when
        UserAccountDTO userAccountDTO = userAccountService.modifyUserAccount("fpg123",
            modifyRequest.toDTO());
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
        assertThatThrownBy(
            () -> userAccountService.modifyUserAccount("wrongUserId", modifyRequest.toDTO()))
            .isInstanceOf(UserAccountNotFoundException.class);
    }
}