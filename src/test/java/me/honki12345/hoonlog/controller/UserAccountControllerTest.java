package me.honki12345.hoonlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import me.honki12345.hoonlog.dto.request.SignUpRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.UserAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("E2E 컨트롤러 테스트")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAccountControllerTest {
    @Autowired UserAccountController userAccountController;
    @Autowired UserAccountService userAccountService;
    @Autowired UserAccountRepository userAccountRepository;
    @Autowired ObjectMapper objectMapper;

    @LocalServerPort private int port;

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입을 성공한다")
    @Test
    void givenSignUpRequest_whenSignUp_thenReturnUserAccount() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("fpg123", "12345678", "fpg123@mail.com");
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .post("/api/v1/users")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat((Integer) extract.jsonPath().get("id")).isNotNull(),
                () -> assertThat((String) extract.jsonPath().get("createdAt")).isNotNull()
        );
    }

    @DisplayName("아이디를 입력하지 않으면, 회원가입 시, 실패한다")
    @Test
    void givenNullUserId_whenSignUp_thenException() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(null, "12345678", "fpg123@mail.com");
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .post("/api/v1/users")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat((String) extract.jsonPath().get("code")).isEqualTo("COMMON1"),
                () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("아이디를 입력해주세요")
        );
    }

    @DisplayName("중복된 아이디를 입력하면, 회원가입 시, 실패한다")
    @Test
    void givenDuplicatedUserId_whenSignUp_thenException() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("fpg123", "12345678", "fpg123@mail.com");
        userAccountService.saveUserAccount(request);
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .post("/api/v1/users")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat((String) extract.jsonPath().get("code")).isEqualTo("USER1"),
                () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("중복된 값이 존재합니다")
        );
    }

}