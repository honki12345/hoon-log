package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static me.honki12345.hoonlog.error.ErrorCode.IMAGE_UPLOAD_ERROR;
import static me.honki12345.hoonlog.util.TestUtils.TEST_FILE_ORIGINAL_NAME;
import static me.honki12345.hoonlog.util.TestUtils.TEST_POST_CONTENT;
import static me.honki12345.hoonlog.util.TestUtils.TEST_POST_TITLE;
import static me.honki12345.hoonlog.util.TestUtils.TEST_TAG_NAME;
import static me.honki12345.hoonlog.util.TestUtils.TEST_UPDATED_POST_CONTENT;
import static me.honki12345.hoonlog.util.TestUtils.TEST_UPDATED_POST_TITLE;
import static me.honki12345.hoonlog.util.TestUtils.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.util.FileUtils;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.dto.TagDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.TagRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.TagService;
import me.honki12345.hoonlog.service.UserAccountService;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("E2E PostController 컨트롤러 테스트")
class PostControllerTest extends IntegrationTestSupport {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FileUtils fileUtils;
    @Autowired
    TestUtils testUtils;
    @Autowired
    AuditorAware<String> auditorAwareForTest;

    @Autowired
    PostController postController;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    TagService tagService;
    @Autowired
    AuthService authService;
    @Autowired
    PostService postService;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[생성/성공]게시글 생성에 성공한다.")
    @Test
    void givenPostInfo_whenAddingPost_thenReturnsSavedPostInfo() {
        // given // when
        TokenDTO tokenDTO = testUtils.createTokensAfterSavingTestUser();
        String fullPathName = testUtils.createImageFilePath(TEST_FILE_ORIGINAL_NAME);

        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .multiPart("title", TEST_POST_TITLE)
                .multiPart("content", TEST_POST_CONTENT)
                .multiPart("tagNames", TEST_TAG_NAME)
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
                "originalImgName", TEST_FILE_ORIGINAL_NAME),
            () -> assertThat(extract.jsonPath()
                .getObject("tagDTOs[0]", TagDTO.class)).hasFieldOrPropertyWithValue(
                "tagName", TEST_TAG_NAME)
        );

    }

    @DisplayName("[조회/성공]게시글 리스트 조회에 성공한다.")
    @Test
    void givenNothing_whenSearchingPost_thenReturnsListOfPostInfo() {
        // given // when
        int postsSize = 5;
        List<Post> postsByTestUser = createPostsByTestUser(postsSize);

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
            () -> assertThat(extract.jsonPath().getList("content")).hasSize(
                postsByTestUser.size()),
            () -> assertThat(postsByTestUser.size()).isEqualTo(postsSize)
        );
    }

    @DisplayName("[조회/성공]게시글 리스트 조회에 성공한다.(keyword is whitespace)")
    @Test
    void givenNothingVersionWhitespace_whenSearchingPost_thenReturnsListOfPostInfo() {
        // given // when
        int postsSize = 5;
        List<Post> postsByTestUser = createPostsByTestUser(postsSize);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .queryParam("keyword", " ")
                .when()
                .get("/api/v1/posts")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getList("content")).hasSize(
                postsByTestUser.size()),
            () -> assertThat(postsByTestUser.size()).isEqualTo(postsSize)
        );
    }


    @DisplayName("[조회/성공]주어진 태그이름으로, 게시글 리스트 조회에 성공한다.")
    @Test
    void givenTagName_whenSearchingPostByTag_thenReturnsListOfPostInfo() {
        // given // when
        String tagName = "hi";
        int postsSize = 5;
        Tag savedTag = tagRepository.save(Tag.of(tagName));
        List<Post> posts = createPostsWithTagByTestUser(postsSize, savedTag);
        postRepository.saveAll(posts);
        tagRepository.save(savedTag);

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .queryParam("tagName", tagName)
                .get("/api/v1/posts/tag")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getList("content")).hasSize(posts.size()),
            () -> assertThat(posts.size()).isEqualTo(postsSize)
        );
    }

    @DisplayName("[조회/성공]주어진 키워드로, 게시글 리스트 조회에 성공한다.")
    @Test
    void givenKeyword_whenSearchingPostByKeyword_thenReturnsListOfPostInfo() {
        // given // when
        String keyword = "1";
        testUtils.saveTestUser();
        List<List<String>> titleContentList = List.of(
            List.of("title1", "content9"),
            List.of("title2", "content9"),
            List.of("title3", "content1"),
            List.of("title4", "content9")
        );
        testUtils.createPostByTestUser(titleContentList.get(0).get(0),
            titleContentList.get(0).get(1));
        testUtils.createPostByTestUser(titleContentList.get(1).get(0),
            titleContentList.get(1).get(1));
        testUtils.createPostByTestUser(titleContentList.get(2).get(0),
            titleContentList.get(2).get(1));
        testUtils.createPostByTestUser(titleContentList.get(3).get(0),
            titleContentList.get(3).get(1));
        long count = titleContentList.stream()
            .filter(strings -> strings.stream().anyMatch(s -> s.contains(keyword))).count();

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .queryParam("keyword", keyword)
                .get("/api/v1/posts")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(extract.jsonPath().getList("content")).hasSize((int) count)
        );
    }


    @DisplayName("[조회/성공]게시글 상세조회에 성공한다.")
    @Test
    void givenPostId_whenSearchingPost_thenReturnsFoundPostInfo() {
        // given // when
        String title = "title";
        String content = "content";
        testUtils.saveTestUser();
        Post createdPost = testUtils.createPostByTestUser(title, content);
        testUtils.createTagByTestUser(createdPost);
        testUtils.createCommentByTestUser(createdPost.getId());

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
    void givenUpdatingInfo_whenUpdatingPost_thenReturnsUpdatedPostInfo() {
        // given // when
        TokenDTO tokenDTO = testUtils.createTokensAfterSavingTestUser();
        Post createdPost = testUtils.createPostByTestUser("title", "content");

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
            () -> assertThat(extract.jsonPath().getString("title")).isEqualTo(
                TEST_UPDATED_POST_TITLE),
            () -> assertThat(extract.jsonPath().getString("content")).isEqualTo(
                TEST_UPDATED_POST_CONTENT)
        );
    }

    @DisplayName("[수정/실패]잘못된 파일로 업로드 요청시, 게시글 수정에 실패한다.")
    @Test
    void givenEmptyFile_whenUpdatingPost_thenReturnsErrorMessage() throws IOException {
        // given // when
        MultipartFile mockMultipartFile = testUtils.createMockMultipartFile("mockFile.jpg");
        PostImage postImage = fileUtils.fromMultipartFileToPostImage(mockMultipartFile);
        postImageRepository.save(postImage);
        TokenDTO tokenDTO = testUtils.createTokensAfterSavingTestUser();
        Post createdPost = testUtils.createPostByTestUser("title", "content");

        Path path = Path.of(testUtils.createImageFilePath("empty-file"));
        if (Files.exists(path)) {
            Files.delete(path);
        }
        File emptyFile = Files.createFile(path).toFile();

        ExtractableResponse<Response> extract =
            given().log().all()
                .port(port)
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .pathParam("postId", createdPost.getId())
                .multiPart("title", TEST_UPDATED_POST_TITLE)
                .multiPart("content", TEST_UPDATED_POST_CONTENT)
                .multiPart("postImageFiles", emptyFile)
                .multiPart("postImageIds", postImage.getId())
                .when()
                .put("/api/v1/posts/{postId}")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(extract.jsonPath().getString("message")).isEqualTo(IMAGE_UPLOAD_ERROR.getMessage()),
            () -> assertThat(extract.jsonPath().getString("code")).isEqualTo(IMAGE_UPLOAD_ERROR.getCode())
        );
    }


    @DisplayName("[삭제/성공]게시글 삭제에 성공한다.")
    @Test
    void givenPostId_whenDeletingPost_thenReturnsOK() {
        // given // when
        TokenDTO tokenDTO = testUtils.createTokensAfterSavingTestUser();
        Post createdPost = testUtils.createPostByTestUser("title", "content");

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

    private List<Post> createPostsByTestUser(int count) {
        testUtils.createTokensAfterSavingTestUser();
        UserAccount userAccount = userAccountRepository.findByUsername(TEST_USERNAME)
            .orElseThrow();
        List<Post> posts = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            PostRequest postRequest = PostRequest.of(TEST_POST_TITLE + i, TEST_POST_CONTENT + i);
            posts.add(postRepository.save(
                postRequest.toDTO().toEntity().addUserAccount(userAccount)));
        }
        return posts;
    }

    private List<Post> createPostsWithTagByTestUser(int count, Tag tag) {
        testUtils.createTokensAfterSavingTestUser();
        UserAccount userAccount = userAccountRepository.findByUsername(TEST_USERNAME)
            .orElseThrow();
        List<Post> posts = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            PostRequest postRequest = PostRequest.of(TEST_POST_TITLE + i, TEST_POST_CONTENT + i);
            Post savedPost = postRepository.save(
                postRequest.toDTO().toEntity().addUserAccount(userAccount)).addTags(Set.of(tag));
            posts.add(savedPost);
        }
        return posts;
    }

}