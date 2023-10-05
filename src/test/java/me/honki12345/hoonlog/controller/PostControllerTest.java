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
import java.io.File;
import java.util.Optional;
import java.util.StringJoiner;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostImageDTO;
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
        TokenDTO tokenDTO = testUtil.createTokensAfterSavingTestUser(username, "12345678");

        StringJoiner sj = new StringJoiner(File.separator);
        String pathname = System.getProperty("user.dir") + File.separator + "src";
        String fileOriginalName = "drawing.jpg";
        String fullPathName = sj.add(pathname).add("test").add("data").add(fileOriginalName)
            .toString();

        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .multiPart("title", TEST_POST_TITLE)
                .multiPart("content", TEST_POST_CONTENT)
                .multiPart("postImageFiles", new File(fullPathName))
                .when()
                .post("/api/v1/posts")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(TEST_POST_TITLE),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(TEST_POST_CONTENT),
            () -> assertThat(extract.jsonPath()
                .getObject("postImageDTOs[0]", PostImageDTO.class)).hasFieldOrPropertyWithValue(
                "originalImgName", fileOriginalName)
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
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
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
        TokenDTO tokenDTO = testUtil.createTokensAfterSavingTestUser();
        Post createdPost = testUtil.createPostWithTestUser("title", "content");

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("postId", createdPost.getId())
                .multiPart("title", TEST_UPDATED_POST_TITLE)
                .multiPart("content", TEST_UPDATED_POST_CONTENT)
                .when()
                .put("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getString("createdBy")).isEqualTo(TEST_USERNAME),
            () -> assertThat(extract.jsonPath().getLong("id")).isEqualTo(createdPost.getId()),
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(TEST_UPDATED_POST_TITLE),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(TEST_UPDATED_POST_CONTENT)
        );
    }

    @DisplayName("[삭제/성공]게시글 삭제에 성공한다.")
    @Test
    void givenPostId_whenDeletingPost_thenReturnsOK() {
        // given // when
        TokenDTO tokenDTO = testUtil.createTokensAfterSavingTestUser();
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
        testUtil.createTokensAfterSavingTestUser(TEST_USERNAME, TEST_PASSWORD);
        for (int i = 0; i < 10; i++) {
            PostRequest postRequest = new PostRequest("title" + i, "content" + i, null);
            userAccountRepository.findByUsername(TEST_USERNAME).ifPresent(userAccount ->
                postRepository.save(postRequest.toDTO().toEntity().addUserAccount(userAccount)));
        }
    }

    @WithMockCustomUser
    private Post createPostWithMockCustomer(String title, String content) {
        testUtil.createTokensAfterSavingTestUser(TEST_USERNAME, TEST_PASSWORD);
        PostRequest postRequest = new PostRequest(title, content, null);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }
}