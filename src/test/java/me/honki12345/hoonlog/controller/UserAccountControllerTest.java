package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.error.ErrorCode.MODIFY_USER_ACCOUNT_FORBIDDEN;
import static me.honki12345.hoonlog.error.ErrorCode.SEARCH_USER_ACCOUNT_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("E2E UserAccount 컨트롤러 테스트")
class UserAccountControllerTest extends IntegrationTestSupport {

    @Autowired
    UserAccountController userAccountController;
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    AuthService authService;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[생성/성공]회원가입을 성공한다")
    @Test
    void givenSignUpRequest_whenSignUp_thenReturnUserAccount() throws Exception {
        // given // when
        String username = "fpg123";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(username, "12345678", email,
            profileDTO);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat((Integer) extract.jsonPath().get("id")).isNotNull(),
            () -> assertThat((String) extract.jsonPath().get("username")).isEqualTo(username),
            () -> assertThat((String) extract.jsonPath().get("email")).isEqualTo(email),
            () -> assertThat((String) extract.jsonPath().get("createdAt")).isNotNull()
        );
    }

    @DisplayName("[생성/실패]아이디를 입력하지 않으면, 회원가입 시, 실패한다")
    @Test
    void givenNullUserId_whenSignUp_thenException() throws Exception {
        // given // when
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            null, "12345678", "fpg123@mail.com", profileDTO);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON)
                .when()
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
        // given // when
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", profileDTO);
        userAccountService.saveUserAccount(request.toDTO());

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON).when()
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

    @DisplayName("[생성/실패]개인블로그 제목을 입력하지 않으면, 회원가입 시, 실패한다")
    @Test
    void givenNoBlogName_whenSignUp_thenException() throws Exception {
        // given // when
        UserAccountAddRequest request = new UserAccountAddRequest(
            "fpg123", "12345678", "fpg123@mail.com", null);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON).when()
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

    @DisplayName("[조회/성공]로그인한 유저 아이디로, 유저 조회에 성공한다")
    @Test
    void givenUserId_whenSearchingUserDetails_thenReturnUserAccount() {
        // given
        String username = "fpg123";
        String email = "fpg123@mail.com";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, "12345678", email);
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("username", username).when()
                .get("/api/v1/users/{username}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat((String) extract.jsonPath().get("username")).isEqualTo(username),
            () -> assertThat((String) extract.jsonPath().get("email")).isEqualTo(email)
        );
    }

    @DisplayName("[조회/실패]로그인한 유저와 조회 유저가 다른경우, 유저 조회시 예외메시지를 반환한다")
    @Test
    void givenNotLoginUserId_whenSearchingUserDetails_thenReturnErrorMessage() {
        // given
        String anotherUsername = "another";
        String username = "fpg123";
        String email = "fpg123@mail.com";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, "12345678", email);
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        // when
        ExtractableResponse<Response> extract = given().log().all()
            .port(port)
            .header("Authorization", "Bearer " + tokenDTO.accessToken())
            .pathParam("username", anotherUsername).when()
            .get("/api/v1/users/{username}")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                SEARCH_USER_ACCOUNT_FORBIDDEN.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                SEARCH_USER_ACCOUNT_FORBIDDEN.getCode())
        );
    }

    @DisplayName("[수정/성공]유저 프로필 수정에 성공한다")
    @Test
    void givenModifyingInfo_whenModifyingUserProfile_thenReturnModifiedUserAccount()
        throws JsonProcessingException {
        // given
        String username = "fpg123";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, "12345678");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        String modifiedBlogName = "blogName2";
        String modifiedBlogShortBio = "bio2";
        UserAccountModifyRequest request = new UserAccountModifyRequest(
            new ProfileDTO(modifiedBlogName, modifiedBlogShortBio));

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("username", username)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON).when()
                .put("/api/v1/users/{username}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat((String) extract.jsonPath().get("profile.blogName")).isEqualTo(
                modifiedBlogName),
            () -> assertThat((String) extract.jsonPath().get("profile.blogShortBio")).isEqualTo(
                modifiedBlogShortBio)
        );
    }

    @DisplayName("[수정/실패]로그인한 유저와 수정 유저가 다른 경우, 유저 프로필 수정시, 에러메세지를 반환한다")
    @Test
    void givenNotLoginUserId_whenModifyingUserProfile_thenReturnsErrorMessage()
        throws JsonProcessingException {
        // given // when
        String anotherUsername = "another";
        String username = "fpg123";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, "12345678");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        String modifiedBlogName = "blogName2";
        String modifiedBlogShortBio = "bio2";
        UserAccountModifyRequest request = new UserAccountModifyRequest(
            new ProfileDTO(modifiedBlogName, modifiedBlogShortBio));

        ExtractableResponse<Response> extract = given().log().all()
            .port(port)
            .header("Authorization", "Bearer " + tokenDTO.accessToken())
            .pathParam("username", anotherUsername)
            .body(objectMapper.writeValueAsString(request))
            .contentType(ContentType.JSON)
            .when()
            .put("/api/v1/users/{username}")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                MODIFY_USER_ACCOUNT_FORBIDDEN.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                MODIFY_USER_ACCOUNT_FORBIDDEN.getCode())
        );
    }




    @DisplayName("[수정/실패]블로그제목을 미입력시, 유저 프로필 수정에 실패한다")
    @Test
    void givenModifyingInfoWithoutBlogName_whenModifyingUserProfile_thenReturnsErrorMessage()
        throws JsonProcessingException {
        // given
        String username = "fpg123";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, "12345678");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        String modifiedBlogName = null;
        String modifiedBlogShortBio = "bio2";
        UserAccountModifyRequest request = new UserAccountModifyRequest(
            new ProfileDTO(modifiedBlogName, modifiedBlogShortBio));

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("username", username)
                .body(objectMapper.writeValueAsString(request))
                .contentType(ContentType.JSON).when()
                .put("/api/v1/users/{username}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat((String) extract.jsonPath().get("code")).isEqualTo("COMMON1"),
            () -> assertThat((String) extract.jsonPath().get("message")).isEqualTo("블로그 제목을 입력해주세요")
        );
    }
}