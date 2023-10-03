package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.*;
import static me.honki12345.hoonlog.util.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.TestUtil;
import me.honki12345.hoonlog.util.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
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
@Import({TestUtil.class})
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
    UserAccountRepository userAccountRepository;
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
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(title),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(content)
        );
    }

    @DisplayName("[조회/성공]게시글 리스트 조회에 성공한다.")
    @Test
    void givenNothing_whenSearchingPost_thenReturnsListOfPostInfo() {
        // given // when
        createPostsWithMockCustomer();

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
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

    @DisplayName("[조회/성공]게시글 조회에 성공한다.")
    @Test
    void givenPostId_whenSearchingPost_thenReturnsFoundPostInfo() {
        // given // when
        String title = "title";
        String content = "content";
        Post createdPost = createPostWithMockCustomer(title, content);
        assert createdPost != null;

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .pathParam("postId", createdPost.getId())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME ),
            () -> assertThat(extract.jsonPath().getLong("id")).isEqualTo(createdPost.getId()),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(title),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(content)
        );
    }

    @DisplayName("[조회/실패]존재하지 않는 게시글 번호 입력으로, 게시글 조회 시, 에러메세지를 반환한다.")
    @Test
    void givenWrongPostId_whenSearchingPost_thenReturnsErrorMessage() {
        // given // when
        long wrongPostId = 5L;

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .pathParam("postId", wrongPostId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo("게시글을 찾을 수 없습니다"),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo("POST1")
        );
    }

    @DisplayName("[수정/성공]게시글 수정에 성공한다.")
    @Test
    void givenUpdatingInfo_whenUpdatingPost_thenReturnsUpdatedPostInfo()
        throws JsonProcessingException {
        // given // when
        TokenDTO tokenDTO = testUtil.createTokensAfterSaving();
        Post createdPost = testUtil.createPostWithTestUser("title", "content");
        String newTitle = "newTitle";
        String newContent = "newContent";
        PostRequest updateRequest = new PostRequest(newTitle, newContent);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("postId", createdPost.getId())
                .body(objectMapper.writeValueAsString(updateRequest))
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME ),
            () -> assertThat(extract.jsonPath().getLong("id")).isEqualTo(createdPost.getId()),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(newTitle),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(newContent)
        );
    }

    @DisplayName("[삭제/성공]게시글 삭제에 성공한다.")
    @Test
    void givenPostId_whenDeletingPost_thenReturnsOK()
        throws JsonProcessingException {
        // given // when
        TokenDTO tokenDTO = testUtil.createTokensAfterSaving();
        Post createdPost = testUtil.createPostWithTestUser("title", "content");

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("postId", createdPost.getId())
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }


    @WithMockCustomUser
    private void createPostsWithMockCustomer() {
        testUtil.createTokensAfterSaving(TEST_USERNAME, TEST_PASSWORD);
        for (int i = 0; i < 10; i++) {
            PostRequest postRequest = new PostRequest("title" + i, "content" + i);
            userAccountRepository.findByUsername(TEST_USERNAME).ifPresent(userAccount ->
                postRepository.save(postRequest.toDTO().toEntity().addUserAccount(userAccount)));
        }
    }

    @WithMockCustomUser
    private Post createPostWithMockCustomer(String title, String content) {
        testUtil.createTokensAfterSaving(TEST_USERNAME, TEST_PASSWORD);
        PostRequest postRequest = new PostRequest(title, content);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }
}