package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.util.PostCommentBuilder.TEST_COMMENT_CONTENT;
import static me.honki12345.hoonlog.util.UserAccountBuilder.TEST_USERNAME;
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
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.PostBuilder;
import me.honki12345.hoonlog.util.PostCommentBuilder;
import me.honki12345.hoonlog.util.TokenBuilder;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;

@DisplayName("E2E PostCommentController 컨트롤러 테스트")
class PostCommentControllerTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuditorAware<String> auditorAwareForTest;
    @Autowired
    private UserAccountBuilder userAccountBuilder;
    @Autowired
    private TokenBuilder tokenBuilder;
    @Autowired
    private PostBuilder postBuilder;
    @Autowired
    private PostCommentBuilder postCommentBuilder;

    @Autowired
    private PostController postController;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private AuthService authService;
    @Autowired
    private PostService postService;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        postCommentBuilder.deleteAllInBatch();
        postBuilder.deleteAllInBatch();
        tokenBuilder.deleteAllInBatch();
        userAccountBuilder.deleteAllInBatch();
    }

    @DisplayName("[생성/성공]댓글 생성에 성공한다.")
    @Test
    void givenPostCommentInfo_whenAddingPostComment_thenReturnsSavedPostCommentInfo()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = tokenBuilder.createTokensAfterSavingTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TEST_COMMENT_CONTENT);

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

    @DisplayName("[수정/성공]댓글 수정에 성공한다.")
    @Test
    void givenPostCommentInfo_whenUpdatingPostComment_thenReturnsUpdatedPostCommentInfo()
        throws JsonProcessingException {
        // given
        TokenDTO tokenDTO = tokenBuilder.createTokensAfterSavingTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment commentWithTestUser = postCommentBuilder.createCommentByTestUser(post.getId());
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

    @DisplayName("[삭제/성공]댓글 삭제에 성공한다.")
    @Test
    void givenPostCommentIdWithUserInfo_whenDeletingPostComment_thenReturnsOK() {
        // given
        TokenDTO tokenDTO = tokenBuilder.createTokensAfterSavingTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment commentWithTestUser = postCommentBuilder.createCommentByTestUser(post.getId());

        // when
        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .pathParam("commentId", commentWithTestUser.getId())
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/comments/{commentId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(
                postCommentRepository.existsById(commentWithTestUser.getId())).isFalse()
        );
    }

}