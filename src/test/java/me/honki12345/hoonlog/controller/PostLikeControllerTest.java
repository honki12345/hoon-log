package me.honki12345.hoonlog.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.service.PostLikeService;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.PostBuilder;
import me.honki12345.hoonlog.util.TokenBuilder;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;

@DisplayName("E2E PostController 컨트롤러 테스트")
class PostLikeControllerTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserAccountBuilder userAccountBuilder;
    @Autowired
    private PostBuilder postBuilder;
    @Autowired
    private TokenBuilder tokenBuilder;
    @Autowired
    private AuditorAware<String> auditorAwareForTest;

    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private PostLikeRepository postLikeRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        tokenBuilder.deleteAllInBatch();
        postBuilder.deleteAllInBatch();
        userAccountBuilder.deleteAllInBatch();
    }

    @DisplayName("게시물좋아요에 성공한다")
    @Test
    void givenPostLikeInfo_whenAddingPostLike_thenReturnsCreated() throws JsonProcessingException {
        // given // when
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        TokenDTO tokenDTO = tokenBuilder.createTokens(userAccountDTO);
        Post postByTestUser = postBuilder.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(postByTestUser.getId(), userAccountDTO.id());

        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(postLikeDTO))
                .when()
                .post("/api/v1/posts/like")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        );
    }

    @DisplayName("게시물좋아요 취소에 성공한다")
    @Test
    void givenPostLikeInfo_whenDeletingPostLike_thenReturnsOK() throws JsonProcessingException {
        // given // when
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        TokenDTO tokenDTO = tokenBuilder.createTokens(userAccountDTO);
        Post postByTestUser = postBuilder.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(postByTestUser.getId(), userAccountDTO.id());
        postLikeService.create(postLikeDTO);

        ExtractableResponse<Response> extract =
            given().log().all()
                .header("Authorization", "Bearer " + tokenDTO.accessToken())
                .port(port)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(postLikeDTO))
                .when()
                .delete("/api/v1/posts/like")
                .then().log().all()
                .extract();

        // then
        assertAll(
            () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK .value())
        );
    }

}