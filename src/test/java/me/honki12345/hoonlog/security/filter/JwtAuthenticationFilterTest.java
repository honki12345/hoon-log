package me.honki12345.hoonlog.security.filter;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import me.honki12345.hoonlog.config.TestJpaConfig;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("E2E JwtAuthenticationFilter 테스트")
@Import({TestUtils.class, TestJpaConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtAuthenticationFilterTest {

    @Value("${jwt.secretKey}")
    String accessSecret;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenizer jwtTokenizer;
    @Autowired
    TestUtils testUtils;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[실패]요청 헤더 액세스토큰의 algorithm 가 유효하지 않으면, 토큰검사시, 예외를 던진다")
    @Test
    void givenInvalidAlgorithm_whenParsingJWT_thenThrowsException() {
        // given // when
        testUtils.createTokensAfterSavingTestUser();
        Post createdPost = testUtils.createPostByTestUser("title", "content");
        String jwsByRSA = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJSUzI1NmluT1RBIiwibmFtZSI6IkpvaG4gRG9lIn0.ICV6gy7CDKPHMGJxV80nDZ7Vxe0ciqyzXD_Hr4mTDrdTyi6fNleYAyhEZq2J29HSI5bhWnJyOBzg2bssBUKMYlC2Sr8WFUas5MAKIr2Uh_tZHDsrCxggQuaHpF4aGCFZ1Qc0rrDXvKLuk1Kzrfw1bQbqH6xTmg2kWQuSGuTlbTbDhyhRfu1WDs-Ju9XnZV-FBRgHJDdTARq1b4kuONgBP430wJmJ6s9yl3POkHIdgV-Bwlo6aZluophoo5XWPEHQIpCCgDm3-kTN_uIZMOHs2KRdb6Px-VN19A5BYDXlUBFOo-GvkCBZCgmGGTlHF_cWlDnoA9XTWWcIYNyUI4PXNw";

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + jwsByRSA)
                .pathParam("postId", createdPost.getId())
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                TOKEN_UNSUPPORTED.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                TOKEN_UNSUPPORTED.getCode())
        );
    }


    @DisplayName("[실패]요청 헤더에 포함된 액세스토큰의 secret key 가 유효하지 않으면, 토큰검사시, 예외를 던진다")
    @Test
    void givenTokenInvalidSecretKey_whenParsingJWT_thenThrowsException() {
        // given // when
        testUtils.createTokensAfterSavingTestUser();
        Post createdPost = testUtils.createPostByTestUser("title", "content");
        String token = createToken(1L, TestUtils.TEST_USERNAME, List.of("USER_ROLE"),
            new Date().getTime() + 299329392L, "시크리코드를위한대충열여섯글자를채우기위한내용입니다");
//        String jwsByInvalidSecretKey = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.pBhe1zK7tdjOq_AQTMm0eu8nAWBMKErFQ2j8Fmvsy6GMDX6bldwsD87ZqJ_kkbtI";

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .pathParam("postId", createdPost.getId())
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(
                HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }

    @DisplayName("[실패]JWT가 헤더에 없다면, 인증이 필요한 요청에, 400 에러메세지를 보낸다")
    @Test
    void givenWithoutAuthorizationHeader_whenDeletingPost_thenReturnsErrorMessage() {
        // given // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .pathParam("postId", 1L)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                TOKEN_INVALID.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                TOKEN_INVALID.getCode())
        );

    }

    @DisplayName("[실패]Authorization 헤더 prefix가 적절하지 않다면, 인증이 필요한 요청에, 400 에러메세지를 보낸다")
    @Test
    void givenAuthorizationHeaderInvalidPrefix_whenDeletingPost_thenReturnsErrorMessage() {
        // given // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .headers("Authorization", "hello")
                .pathParam("postId", 1L)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                TOKEN_INVALID.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                TOKEN_INVALID.getCode())
        );
    }

    @DisplayName("[실패]만료시간이 지난 JWT로, 인증이필요한 요청에, 400 에러메세지를 보낸다")
    @Test
    void givenExpiredToken_whenDeletingPost_thenReturnsErrorMessage() {
        // given // when
        testUtils.createTokensAfterSavingTestUser();
        testUtils.createPostByTestUser("title", "content");
        String token = createToken(232939L);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .pathParam("postId", 1L)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(
                TOKEN_EXPIRED.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(
                TOKEN_EXPIRED.getCode())
        );
    }


    @DisplayName("[실패]JWT 토큰 페이로드의 id 가 숫자가 아니면, JWT 파싱 중, 500 에러메세지를 보낸다")
    @Test
    void givenJWTInvalidPayLoad_whenDeletingPost_thenReturnsErrorMessage() {
        // given // when
        String token = createToken("hi", TestUtils.TEST_USERNAME, List.of("USER_ROLE"),
            (new Date().getTime() + 39394939L), accessSecret);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .pathParam("postId", 1L)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private String createToken(Long expire) {
        return createToken(1L, TestUtils.TEST_USERNAME, List.of("USER_ROLE"), expire, accessSecret);
    }

    private String createToken(Object id, String username, List<String> roles,
        Long expire, String secretKeyStr) {
        byte[] secretKey = secretKeyStr.getBytes(StandardCharsets.UTF_8);
        Claims claims = Jwts.claims();
        claims.put("roles", roles);
        claims.put("id", id);
        claims.put("name", username);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(expire))
            .signWith(getSigningKey(secretKey))
            .compact();
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

}
