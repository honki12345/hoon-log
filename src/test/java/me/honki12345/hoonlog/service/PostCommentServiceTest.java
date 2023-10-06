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
import me.honki12345.hoonlog.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("PostCommentService 애플리케이션 통합테스트")
@Import({TestUtil.class})
@ActiveProfiles("test")
@SpringBootTest
class PostCommentServiceTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtil testUtil;

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    PostCommentService postCommentService;

    @AfterEach
    void tearDown() {
        testUtil.deleteAllInBatchInAllRepository();
    }

    @DisplayName("댓글 생성에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenAddingComment_thenReturnsSavedPostComment() {
        // given
        UserAccountDTO userAccountDTO = testUtil.saveTestUser();
        Post post = testUtil.createPostWithTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TestUtil.TEST_COMMENT_CONTENT);

        // when
        PostCommentDTO postCommentDTO = postCommentService.addPostComment(
            postCommentRequest.toDTO(), post.getId(), userAccountDTO);

        // then
        assertThat(postCommentDTO.id()).isEqualTo(post.getId());
        assertThat(postCommentDTO.createdBy()).isEqualTo(userAccountDTO.username());
        assertThat(postCommentDTO.content()).isEqualTo(TestUtil.TEST_COMMENT_CONTENT);
    }

    @DisplayName("댓글 수정에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenUpdatingComment_thenReturnsUpdatedPostComment() {
        // given
        UserAccountDTO userAccountDTO = testUtil.saveTestUser();
        Post post = testUtil.createPostWithTestUser();
        PostComment savedPostComment = testUtil.createCommentWithTestUser(post.getId());
        String updatedContent = "updatedContent";
        PostCommentRequest updateRequest = new PostCommentRequest(post.getId(), updatedContent);

        // when
        PostCommentDTO savedPostCommentDTO = postCommentService.modifyComment(updateRequest.toDTO(),
            savedPostComment.getId(),
            userAccountDTO);

        // then
        assertThat(savedPostCommentDTO.id()).isEqualTo(post.getId());
        assertThat(savedPostCommentDTO.createdBy()).isEqualTo(userAccountDTO.username());
        assertThat(savedPostCommentDTO.content()).isEqualTo(updatedContent);
    }

    @DisplayName("댓글 삭제에 성공한다.")
    @Test
    void givenCommentIdWithUserInfo_whenDeletingComment_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtil.saveTestUser();
        Post post = testUtil.createPostWithTestUser();
        PostComment savedPostComment = testUtil.createCommentWithTestUser(post.getId());

        // when
        postCommentService.deleteComment(savedPostComment.getId(), userAccountDTO);

        // then
        assertThat(postCommentRepository.existsById(savedPostComment.getId())).isFalse();
    }
}