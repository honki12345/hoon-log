package me.honki12345.hoonlog.service;

import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.config.error.exception.UserAccountNotFoundException;
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

@DisplayName("애플리케이션 통합테스트")
@ActiveProfiles("test")
@SpringBootTest
class UserAccountServiceTest {
    @Autowired private UserAccountService userAccountService;
    @Autowired private UserAccountRepository userAccountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

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
        assertThatThrownBy(() -> userAccountService.saveUserAccount(request)).isInstanceOf(DuplicateUserAccountException.class);
    }

    @DisplayName("[조회/실패]존재하지 않는 유저 아이디로, 회원조회 시, 예외를 발생한다.")
    @Test
    void givenNotFoundUserId_whenFindUserAccount_thenThrowingException() {
        // given // when // then
        assertThatThrownBy(() -> userAccountService.findUserAccount("fpg123")).isInstanceOf(UserAccountNotFoundException.class);
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
        UserAccountDTO userAccountDTO = userAccountService.modifyUserAccount("fpg123", modifyRequest);
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

}