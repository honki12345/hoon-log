package me.honki12345.hoonlog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.UserAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

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

    @DisplayName("[생성/성공]회원가입을 성공한다")
    @Test
    void givenSignUpRequest_whenSignUp_thenReturnUserAccount() throws Exception {
        // given
        String userId = "fpg123";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(userId, "12345678", email, profileDTO);
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
                () -> assertThat((String) extract.jsonPath().get("userId")).isEqualTo(userId),
                () -> assertThat((String) extract.jsonPath().get("email")).isEqualTo(email),
                () -> assertThat((String) extract.jsonPath().get("createdAt")).isNotNull()
        );
    }

    @DisplayName("[생성/실패]아이디를 입력하지 않으면, 회원가입 시, 실패한다")
    @Test
    void givenNullUserId_whenSignUp_thenException() throws Exception {
        // given
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
                null, "12345678", "fpg123@mail.com", profileDTO);
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

    @DisplayName("[생성/실패]중복된 아이디를 입력하면, 회원가입 시, 실패한다")
    @Test
    void givenDuplicatedUserId_whenSignUp_thenException() throws Exception {
        // given
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
                "fpg123", "12345678", "fpg123@mail.com", profileDTO);
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

    @DisplayName("[생성/실패]블로그 제목을 입력하지 않으면, 회원가입 시, 실패한다")
    @Test
    void givenNoBlogName_whenSignUp_thenException() throws Exception {
        // given
        UserAccountAddRequest request = new UserAccountAddRequest(
                "fpg123", "12345678", "fpg123@mail.com", null);
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
                () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("블로그 제목을 입력해주세요")
        );
    }

    @DisplayName("[조회/성공]유저 아이디로, 유저 조회에 성공한다")
    @Test
    void givenUserId_whenSearchingUserDetails_thenReturnUserAccount() {
        // given
        String userId = "fpg123";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        saveOneUserAccount(userId, email, profileDTO);

        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .pathParam("userId", userId);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .get("/api/v1/users/{userId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat((String) extract.jsonPath().get("userId")).isEqualTo(userId),
                () -> assertThat((String) extract.jsonPath().get("email")).isEqualTo(email)
        );
    }

    @DisplayName("[조회/실패]존재하지 않는 유저 아이디면, 유저 조회시, 실패한다")
    @Test
    void givenNotFoundUserId_whenSearchingUserDetails_thenThrowsException() {
        // given
        String userId = "fpg123";
        String email = "fpg123@mail.com";
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .pathParam("userId", userId);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .get("/api/v1/users/{userId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat((String) extract.jsonPath().get("code")).isEqualTo("USER2"),
                () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("존재하지 않는 값입니다")
        );
    }

    @DisplayName("[수정/성공]유저 프로필 수정에 성공한다")
    @Test
    void givenModifyingInfo_whenModifyingUserProfile_thenReturnModifiedUserAccount() throws JsonProcessingException {
        // given
        String userId = "fpg123";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        saveOneUserAccount(userId, email, profileDTO);

        String modifiedBlogName = "blogName2";
        String modifiedBlogShortBio = "bio2";
        UserAccountModifyRequest request = new UserAccountModifyRequest(new ProfileDTO(modifiedBlogName, modifiedBlogShortBio));
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .pathParam("userId", userId)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .put("/api/v1/users/{userId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat((String) extract.jsonPath().get("profile.blogName")).isEqualTo(modifiedBlogName),
                () -> assertThat((String) extract.jsonPath().get("profile.blogShortBio")).isEqualTo(modifiedBlogShortBio)
        );
    }


    @DisplayName("[수정/실패]블로그제목을 미입력시, 유저 프로필 수정에 실패한다")
    @Test
    void givenModifyingInfoWithoutBlogName_whenModifyingUserProfile_thenReturnsErrorMessage() throws JsonProcessingException {
        // given
        String userId = "fpg123";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        saveOneUserAccount(userId, email, profileDTO);

        String modifiedBlogName = null;
        String modifiedBlogShortBio = "bio2";
        UserAccountModifyRequest request = new UserAccountModifyRequest(new ProfileDTO(modifiedBlogName, modifiedBlogShortBio));
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .port(port)
                .pathParam("userId", userId)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
                .put("/api/v1/users/{userId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat((String) extract.jsonPath().get("code")).isEqualTo("COMMON1"),
                () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("블로그 제목을 입력해주세요")
        );
    }


    private void saveOneUserAccount(String userId, String email, ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(userId, "12345678", email, profileDTO);
        userAccountService.saveUserAccount(request);
    }

}