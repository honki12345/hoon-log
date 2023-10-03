package me.honki12345.hoonlog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("E2E AuthController 컨트롤러 테스트")
@Import({TestUtil.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenizer jwtTokenizer;
    @Autowired
    TestUtil testUtil;
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    AuthService authService;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAllInBatch();
    }

    @DisplayName("[로그인/성공]로그인에 성공한다")
    @Test
    void givenLoginInfo_whenLogin_thenReturnsTokens() throws JsonProcessingException {
        // given
        String username = "fpg123";
        String password = "12345678";
        String email = "fpg123@mail.com";
        UserAccountDTO userAccountDTO = testUtil.saveTestUser(username, password, email);

        LoginRequest loginRequest = new LoginRequest(username, password);
        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(loginRequest))
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .post("/api/v1/auth/token")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(extract.jsonPath().getLong("userId")).isEqualTo(
                userAccountDTO.id()),
            () -> assertThat(extract.jsonPath().getString("username")).isEqualTo(
                userAccountDTO.username()),
            () -> assertThat(jwtTokenizer.getUserIdFromAccessToken(
                extract.jsonPath().getString("accessToken"))).isEqualTo(userAccountDTO.id()),
            () -> assertThat(jwtTokenizer.getUserIdFromRefreshToken(
                extract.jsonPath().getString("refreshToken"))).isEqualTo(userAccountDTO.id())
        );
    }

    @DisplayName("[로그인/실패]잘못된 정보를 입력하여, 로그인 시, 에러메세지를 반환한다")
    @Test
    void givenWrongLoginInfo_whenLogin_thenReturnsErrorMessage() throws JsonProcessingException {
        LoginRequest loginRequest = new LoginRequest("fpg123", "12345678");
        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(loginRequest))
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .post("/api/v1/auth/token")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo("LOGIN1"),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo("로그인에 실패하였습니다")
        );
    }

    @DisplayName("[로그아웃/성공]로그아웃에 성공한다")
    @Test
    void givenLogoutInfo_whenLogout_thenReturns200Ok() throws JsonProcessingException {
        // given
        UserAccountDTO userAccountDTO = testUtil.saveTestUser("fpg123", "12345678", "fpg123@mail.com");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(tokenDTO))
            .header("Authorization", "Bearer " + tokenDTO.accessToken())
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .delete("/api/v1/auth/token")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(
                refreshTokenRepository.existsByToken(tokenDTO.refreshToken())).isFalse()
        );
    }


    @DisplayName("[로그아웃/실패]유효하지 않은 액세스 토큰 값일 경우, 로그아웃 시, 에러메시지를 보낸다")
    @Test
    void givenWrongLogoutInfo_whenLogout_thenReturnsErrorMessage() throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = TokenDTO.of("WrongAccessToken", "WrongRefreshToken");

        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(tokenDTO))
            .header("Authorization", "Bearer " + tokenDTO.accessToken())
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .delete("/api/v1/auth/token")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo("TOKEN2"),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo("올바르지 않은 토큰입니다")
        );
    }

    @DisplayName("[재발급/성공]엑세스토큰 재발급을 성공한다")
    @Test
    void givenTokenInfo_whenRefreshingToken_thenReturnsNewAccessToken()
        throws JsonProcessingException {
        // given
        UserAccountDTO userAccountDTO = testUtil.saveTestUser("fpg123", "12345678", "fpg123@mail.com");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(tokenDTO))
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .post("/api/v1/auth/refreshToken")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(jwtTokenizer.getUserIdFromAccessToken(
                extract.jsonPath().getString("accessToken"))).isEqualTo(userAccountDTO.id())
        );
    }

    @DisplayName("[재발급/실패]유효하지 않은 리프레쉬 토큰 입력으로, 토큰 재발급시, 에러 메세지를 반환한다")
    @Test
    void givenWrongTokenInfo_whenRefreshingToken_thenReturnsErrorMessage()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = TokenDTO.of("WrongAccessToken", "WrongRefreshToken");

        RequestSpecification requestSpecification = RestAssured
            .given().log().all()
            .port(port)
            .body(objectMapper.writeValueAsString(tokenDTO))
            .contentType(ContentType.JSON);

        // when
        ExtractableResponse<Response> extract = requestSpecification.when()
            .post("/api/v1/auth/refreshToken")
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo("COMMON4"),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo("존재하지 않는 값입니다")
        );
    }
}