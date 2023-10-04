package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.util.TestUtil.TEST_COMMENT_CONTENT;
import static me.honki12345.hoonlog.util.TestUtil.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.TestUtil;
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

@DisplayName("E2E PostCommentController 컨트롤러 테스트")
@Import({TestUtil.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostCommentControllerTest {

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

    @DisplayName("댓글 생성에 성공한다.")
    @Test
    void givenPostCommentInfo_whenAddingPostComment_thenReturnsSavedPostCommentInfo()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = testUtil.createTokensAfterSavingTestUser();
        Post post = testUtil.createPostWithTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TestUtil.TEST_COMMENT_CONTENT);

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .body(objectMapper.writeValueAsString(postCommentRequest))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(
                TEST_COMMENT_CONTENT)
        );
    }

    @DisplayName("댓글 수정에 성공한다.")
    @Test
    void givenPostCommentInfo_whenUpdatingPostComment_thenReturnsUpdatedPostCommentInfo()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = testUtil.createTokensAfterSavingTestUser();
        Post post = testUtil.createPostWithTestUser();
        PostComment commentWithTestUser = testUtil.createCommentWithTestUser(post.getId());
        PostCommentRequest updateRequest = new PostCommentRequest(post.getId(),
            "newUpdatedComment");

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .body(objectMapper.writeValueAsString(updateRequest))
                .pathParam("commentId", commentWithTestUser.getId())
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/comments/{commentId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo("newUpdatedComment")
        );

    }
}