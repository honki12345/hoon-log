package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.config.JpaConfig;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.TestUtil;
import me.honki12345.hoonlog.util.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("E2E PostController 컨트롤러 테스트")
@Import({TestUtil.class, JpaConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtil testUtil;
    @Autowired
    AuditorAware<String> auditorAwareForTest;

    @Autowired
    PostController postController;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    AuthService authService;
    @Autowired
    PostService postService;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        testUtil.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[생성/성공]게시글 생성에 성공한다.")
    @Test
    void givenPostInfo_whenAddingPost_thenReturnsSavedPostInfo() throws JsonProcessingException {
        // given // when
        String username = "fpg123";
        String title = "title";
        String content = "content";
        TokenDTO tokenDTO = testUtil.createTokensAfterSaving(username, "12345678");
        PostRequest postRequest = new PostRequest(title, content);

        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .body(objectMapper.writeValueAsString(postRequest))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/posts")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(username),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(title),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(content)
        );
    }

    @Disabled
    @DisplayName("[조회/성공]게시글 리스트 조회에 성공한다.")
    @Test
    void givenNothing_whenSearchingPost_thenReturnsListOfPostInfo() throws JsonProcessingException {
        // given // when
        create10Posts();
        String title = "title";
        String content = "content";
        PostRequest postRequest = new PostRequest(title, content);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .body(objectMapper.writeValueAsString(postRequest))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/posts")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getList("content")).hasSize(10)
        );
    }

    @WithMockCustomUser
    private void create10Posts() {
        String username = "fpg123";
        testUtil.createTokensAfterSaving(username, "12345678");
        UserAccountPrincipal userAccountPrincipal = UserAccountPrincipal.of(1L, username);
        for (int i = 0; i < 10; i++) {
            PostRequest postRequest = new PostRequest("title" + i, "content" + i);
            postService.addPost(postRequest, userAccountPrincipal);

        }
    }


}