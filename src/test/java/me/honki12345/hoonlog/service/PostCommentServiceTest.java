package me.honki12345.hoonlog.service;

import static me.honki12345.hoonlog.util.PostCommentBuilder.TEST_COMMENT_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.error.exception.domain.DeleteCommentForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.ModifyCommentForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostCommentNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.PostBuilder;
import me.honki12345.hoonlog.util.PostCommentBuilder;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostCommentService 애플리케이션 통합테스트")
class PostCommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserAccountBuilder userAccountBuilder;
    @Autowired
    private PostBuilder postBuilder;
    @Autowired
    private PostCommentBuilder postCommentBuilder;


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private PostService postService;

    @AfterEach
    void tearDown() {
        postCommentBuilder.deleteAllInBatch();
        postBuilder.deleteAllInBatch();
        userAccountBuilder.deleteAllInBatch();
    }

    @DisplayName("[생성/성공]댓글 생성에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenAddingComment_thenReturnsSavedPostComment() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TEST_COMMENT_CONTENT);

        // when
        PostCommentDTO postCommentDTO = postCommentService.addPostComment(
            postCommentRequest.toDTO(), post.getId(), userAccountDTO);

        // then
        assertThat(postCommentDTO.createdBy()).isEqualTo(userAccountDTO.username());
        assertThat(postCommentDTO.content()).isEqualTo(TEST_COMMENT_CONTENT);
    }

    @DisplayName("[생성/실패]찾을 수 없는 게시물에, 댓글 생성 요청시, 예외를 던진다.")
    @Test
    void givenCommentInfoWithUnSavedPostId_whenAddingComment_thenReturnsThrowsException() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Long unsavedPostId = 999L;
        PostCommentRequest postCommentRequest = new PostCommentRequest(unsavedPostId,
            TEST_COMMENT_CONTENT);

        // when // then
        assertThatThrownBy(() ->
            postCommentService.addPostComment(
                postCommentRequest.toDTO(), unsavedPostId, userAccountDTO))
            .isInstanceOf(PostNotFoundException.class);
    }

    @DisplayName("[생성/실패]유효한 회원이 아닌데, 댓글 생성 요청시, 예외를 던진다.")
    @Test
    void givenCommentInfoWithUnRegisteredUserInfo_whenAddingComment_thenReturnsThrowsException() {
        // given
        long wrongUserId = 999L;
        UserAccountDTO wrongUserAccountDTO = UserAccountDTO.of(wrongUserId, null, List.of());
        userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostCommentRequest postCommentRequest = new PostCommentRequest(post.getId(),
            TEST_COMMENT_CONTENT);

        // when // then
        assertThatThrownBy(() ->
            postCommentService.addPostComment(
                postCommentRequest.toDTO(), post.getId(), wrongUserAccountDTO))
            .isInstanceOf(UserAccountNotFoundException.class);
    }


    @DisplayName("[수정/성공]댓글 수정에 성공한다.")
    @Test
    void givenCommentInfoWithPostIdAndUserInfo_whenUpdatingComment_thenReturnsUpdatedPostComment() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment savedPostComment = postCommentBuilder.createCommentByTestUser(post.getId());
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

    @DisplayName("[수정/실패]유효한 회원이 아닐시, 댓글 수정하면, 예외를 던진다.")
    @Test
    void givenCommentInfoWithUnregisteredUserId_whenUpdatingComment_thenThrowsException() {
        // given
        long wrongUserId = 999L;
        UserAccountDTO wrongUserAccountDTO = UserAccountDTO.of(wrongUserId, null, List.of());
        userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment savedPostComment = postCommentBuilder.createCommentByTestUser(post.getId());
        PostCommentRequest updateRequest = new PostCommentRequest(post.getId(), "updatedContent");

        // when // then
        assertThatThrownBy(() ->
            postCommentService.modifyComment(
                updateRequest.toDTO(),
                savedPostComment.getId(),
                wrongUserAccountDTO))
            .isInstanceOf(ModifyCommentForbiddenException.class);
    }

    @DisplayName("[수정/실패]유효한 댓글이 아닐시, 댓글 수정하면, 예외를 던진다.")
    @Test
    void givenCommentInfoWithUnregisteredCommentId_whenUpdatingComment_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        postCommentBuilder.createCommentByTestUser(post.getId());
        Long wrongCommentId = 999L;
        PostCommentRequest updateRequest = new PostCommentRequest(wrongCommentId, "updatedContent");

        // when // then
        assertThatThrownBy(() ->
            postCommentService.modifyComment(
                updateRequest.toDTO(),
                wrongCommentId,
                userAccountDTO))
            .isInstanceOf(PostCommentNotFoundException.class);
    }


    @DisplayName("[삭제/성공]댓글 삭제에 성공한다.")
    @Test
    void givenCommentIdWithUserInfo_whenDeletingComment_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment savedPostComment = postCommentBuilder.createCommentByTestUser(post.getId());

        // when
        postCommentService.deleteComment(savedPostComment.getId(), userAccountDTO);

        // then
        assertThat(postCommentRepository.existsById(savedPostComment.getId())).isFalse();
    }

    @DisplayName("[삭제/실패]유효한 회원아 아닐시, 댓글 삭제시, 예외를 던진다.")
    @Test
    void givenCommentIdWithUnsavedUserInfo_whenDeletingComment_thenThrowsException() {
        // given
        long wrongUserId = 999L;
        UserAccountDTO wrongUserAccountDTO = UserAccountDTO.of(wrongUserId, null, List.of());
        userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        PostComment savedPostComment = postCommentBuilder.createCommentByTestUser(post.getId());

        // when // then
        assertThatThrownBy(() ->
            postCommentService.deleteComment(savedPostComment.getId(), wrongUserAccountDTO))
            .isInstanceOf(DeleteCommentForbiddenException.class);
    }

    @DisplayName("[삭제/실패]유효한 댓글이 아닐시, 댓글 삭제시, 예외를 던진다.")
    @Test
    void givenCommentIdWithUnRegisteredPostId_whenDeletingComment_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = userAccountBuilder.saveTestUser();
        Post post = postBuilder.createPostByTestUser();
        postCommentBuilder.createCommentByTestUser(post.getId());
        Long wrongCommentId = 999L;

        // when // then
        assertThatThrownBy(() ->
            postCommentService.deleteComment(wrongCommentId, userAccountDTO))
            .isInstanceOf(PostCommentNotFoundException.class);
    }
}