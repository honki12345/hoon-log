package me.honki12345.hoonlog.service;

import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
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

    @DisplayName("유저 정보를 입력하면, 회원가입 시, id와 encoded 비밀번호와 가입일자 정보가 포함된 유저 객체를 생성한다.")
    @Test
    void givenSignUpRequest_whenSignUp_thenContainingIdAndEncodedPWDAndCreatedAt() {
        // given
        String userPassword = "12345678";
        UserAccountAddRequest request = new UserAccountAddRequest("fpg123", userPassword, "fpg123@mail.com");

        // when
        UserAccountDTO actual = userAccountService.saveUserAccount(request);

        // then
        assertThat(actual)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdAt");
        assertThat(passwordEncoder.matches(userPassword, actual.userPassword()));
    }

    @DisplayName("중복된 유저 아이디가 있으면, 회원가입 시, 예외를 발생한다.")
    @Test
    void givenSignUpRequestWithDuplicateUserId_whenSignUp_thenThrowingException() {
        // given
        UserAccountAddRequest request = new UserAccountAddRequest("fpg123", "12345678", "fpg123@mail.com");
        userAccountService.saveUserAccount(request);

        // when // then
        assertThatThrownBy(() -> userAccountService.saveUserAccount(request)).isInstanceOf(DuplicateUserAccountException.class);
    }

}