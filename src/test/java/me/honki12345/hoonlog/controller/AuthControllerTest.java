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
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.provider.JwtTokenProvider;
import me.honki12345.hoonlog.service.UserAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("E2E AuthController 컨트롤러 테스트")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserAccountRepository userAccountRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAllInBatch();
    }

    @DisplayName("로그인에 성공한다")
    @Test
    void givenLoginInfo_whenLogin_thenReturnsTokens() throws JsonProcessingException {
        // given
        String username = "fpg123";
        String password = "12345678";
        String email = "fpg123@mail.com";
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        UserAccountDTO userAccountDTO = saveOneUserAccount(username, email, profileDTO);

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
            () -> assertThat(jwtTokenProvider.getUserIdFromAccessToken(
                extract.jsonPath().getString("accessToken"))).isEqualTo(userAccountDTO.id()),
            () -> assertThat(jwtTokenProvider.getUserIdFromRefreshToken(
                extract.jsonPath().getString("refreshToken"))).isEqualTo(userAccountDTO.id())
        );
    }

    @DisplayName("잘못된 정보를 입력하여, 로그인 시, 에러메세지를 반환한다")
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


    private UserAccountDTO saveOneUserAccount(String username, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, "12345678", email,
            profileDTO);
        return userAccountService.saveUserAccount(request);
    }

}