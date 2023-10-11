package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.error.ErrorCode.LOGOUT_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
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

@DisplayName("E2E AuthController 컨트롤러 테스트")
class AuthControllerTest extends IntegrationTestSupport {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenizer jwtTokenizer;
    @Autowired
    TestUtils testUtils;

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
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[로그인/성공]로그인에 성공한다")
    @Test
    void givenLoginInfo_whenLogin_thenReturnsTokens() throws JsonProcessingException {
        // given
        String username = "fpg123";
        String password = "12345678";
        String email = "fpg123@mail.com";
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(username, password, email);

        LoginRequest loginRequest = new LoginRequest(username, password);

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(loginRequest))
                .contentType(ContentType.JSON).when()
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

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(loginRequest))
                .contentType(ContentType.JSON).when()
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
        UserAccountDTO userAccountDTO = testUtils.saveTestUser("fpg123", "12345678",
            "fpg123@mail.com");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(tokenDTO))
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .contentType(ContentType.JSON).when()
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
    void givenInvalidAccessToken_whenLogout_thenReturnsErrorMessage()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = TokenDTO.of("WrongAccessToken", "WrongRefreshToken");

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(tokenDTO))
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .contentType(ContentType.JSON).when()
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

    @DisplayName("[로그아웃/실패]유효하지 않은 리프레쉬 토큰 값일 경우, 로그아웃 시, 에러메시지를 보낸다")
    @Test
    void givenInvalidRefreshToken_whenLogout_thenReturnsErrorMessage()
        throws JsonProcessingException {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser("fpg123", "12345678",
            "fpg123@mail.com");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        TokenDTO invalidTokenDTO = TokenDTO.of(tokenDTO.accessToken(), "WrongRefreshToken");

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(invalidTokenDTO))
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .contentType(ContentType.JSON).when()
                .delete("/api/v1/auth/token")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                LOGOUT_ERROR.getCode()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                LOGOUT_ERROR.getMessage())
        );
    }

    @DisplayName("[재발급/성공]엑세스토큰 재발급을 성공한다")
    @Test
    void givenTokenInfo_whenRefreshingToken_thenReturnsNewAccessToken()
        throws JsonProcessingException {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser("fpg123", "12345678",
            "fpg123@mail.com");
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(tokenDTO))
                .contentType(ContentType.JSON).when()
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

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(tokenDTO))
                .contentType(ContentType.JSON).when()
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