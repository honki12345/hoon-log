package me.honki12345.hoonlog.service;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("PostCommentService 애플리케이션 통합테스트")
@Import({TestUtils.class})
@ActiveProfiles("test")
@SpringBootTest
class PostCommentServiceTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    PostCommentService postCommentService;
    @Autowired
    PostService postService;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("댓글 생성에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenAddingComment_thenReturnsSavedPostComment() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TestUtils.TEST_COMMENT_CONTENT);

        // when
        PostCommentDTO postCommentDTO = postCommentService.addPostComment(
            postCommentRequest.toDTO(), post.getId(), userAccountDTO);

        // then
        assertThat(postCommentDTO.createdBy()).isEqualTo(userAccountDTO.username());
        assertThat(postCommentDTO.content()).isEqualTo(TestUtils.TEST_COMMENT_CONTENT);
    }

    @DisplayName("댓글 수정에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenUpdatingComment_thenReturnsUpdatedPostComment() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostComment savedPostComment = testUtils.createCommentByTestUser(post.getId());
        String updatedContent = "updatedContent";
        PostCommentRequest updateRequest = new PostCommentRequest(post.getId(), updatedContent);

        // when
        PostCommentDTO savedPostCommentDTO = postCommentService.modifyComment(updateRequest.toDTO(),
            savedPostComment.getId(),
            userAccountDTO);

        // then
        assertThat(savedPostCommentDTO.createdBy()).isEqualTo(userAccountDTO.username());
        assertThat(savedPostCommentDTO.content()).isEqualTo(updatedContent);
    }

    @DisplayName("댓글 삭제에 성공한다.")
    @Test
    void givenCommentIdWithUserInfo_whenDeletingComment_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostComment savedPostComment = testUtils.createCommentByTestUser(post.getId());

        // when
        postCommentService.deleteComment(savedPostComment.getId(), userAccountDTO);

        // then
        assertThat(postCommentRepository.existsById(savedPostComment.getId())).isFalse();
    }
}