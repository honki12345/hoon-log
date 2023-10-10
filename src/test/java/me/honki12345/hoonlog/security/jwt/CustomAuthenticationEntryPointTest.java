package me.honki12345.hoonlog.security.jwt;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@Import({TestUtils.class})
@DisplayName("WebMvcTest - jwt토큰 필터 테스트")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomAuthenticationEntryPointTest {

    @LocalServerPort
    private int port;


}