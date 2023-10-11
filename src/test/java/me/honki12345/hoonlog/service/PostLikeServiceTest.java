package me.honki12345.hoonlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.exception.domain.DuplicatePostLikeException;
import me.honki12345.hoonlog.error.exception.domain.PostLikeNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PostLikeService 애플리케이션 통합테스트")
class PostLikeServiceTest extends IntegrationTestSupport {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;

    @Autowired
    PostLikeService postLikeService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountRepository userAccountRepository;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[생성/성공]게시글좋아요를 성공한다")
    @Test
    void givenPostIdAndUserId_whenCreatingPostLike_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), userAccountDTO.id());

        // when
        postLikeService.create(postLikeDTO);
        Post findPost = postRepository.findById(post.getId()).orElseThrow();

        // then
        assertThat(findPost.getLikeCount()).isEqualTo(1L);
    }

    @DisplayName("[생성/실패]중복 요청시, 게시글좋아요를 하면, 예외를 던진다")
    @Test
    void givenPostIdAndUserId_whenCreatingPostLikeAgain_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), userAccountDTO.id());
        postLikeService.create(postLikeDTO);

        // when // then
        assertThatThrownBy(() -> postLikeService.create(postLikeDTO)).isInstanceOf(
            DuplicatePostLikeException.class);
    }

    @DisplayName("[생성/실패]존재하지 않는 유저로 요청시, 게시글좋아요를 하면, 예외를 던진다")
    @Test
    void givenUnRegisteredUserId_whenCreatingPostLikeAgain_thenThrowsException() {
        // given
        testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        long wrongUserId = 9999L;
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), wrongUserId);

        // when // then
        assertThatThrownBy(() -> postLikeService.create(postLikeDTO)).isInstanceOf(
            UserAccountNotFoundException.class);
    }

    @DisplayName("[생성/실패]존재하지 않는 게시물에 요청시, 게시글좋아요를 하면, 예외를 던진다")
    @Test
    void givenUnsavedPostId_whenCreatingPostLikeAgain_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        long unsavedPostId = 2L;
        PostLikeDTO postLikeDTO = new PostLikeDTO(unsavedPostId, userAccountDTO.id());

        // when // then
        assertThatThrownBy(() -> postLikeService.create(postLikeDTO)).isInstanceOf(
            PostNotFoundException.class);
    }

    @DisplayName("[삭제/성공]게시글좋아요 취소를 성공한다")
    @Test
    void givenPostIdAndUserId_whenDeletingPostLike_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), userAccountDTO.id());
        postLikeService.create(postLikeDTO);

        // when
        postLikeService.delete(postLikeDTO);
        Post findPost = postRepository.findById(post.getId()).orElseThrow();

        // then
        assertThat(findPost.getLikeCount()).isEqualTo(0L);
    }

    @DisplayName("[삭제/실패]좋아요를 한 적이 없다면, 게시글좋아요 취소시, 예외를 던진다")
    @Test
    void givenPostIdAndUserId_whenDeletingUnlikedPostLike_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), userAccountDTO.id());

        // when // then
        assertThatThrownBy(() -> postLikeService.delete(postLikeDTO)).isInstanceOf(
            PostLikeNotFoundException.class);
    }

    @DisplayName("[삭제/실패]존재하지 않는 게시물로, 게시글좋아요 취소시, 예외를 던진다")
    @Test
    void givenUnsavedPostIdAndUserId_whenDeletingPostLike_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        long unsavedPostId = 2L;
        PostLikeDTO postLikeDTO = new PostLikeDTO(unsavedPostId, userAccountDTO.id());

        // when // then
        assertThatThrownBy(() -> postLikeService.delete(postLikeDTO)).isInstanceOf(
            PostNotFoundException.class);
    }

    @DisplayName("[삭제/실패]가입하지 않은 유저정보로, 게시글좋아요 취소시, 예외를 던진다")
    @Test
    void givenPostIdAndUnregisteredUserId_whenDeletingPostLike_thenThrowsException() {
        // given
        testUtils.saveTestUser();
        Post post = testUtils.createPostByTestUser();
        long wrongUserId = 999L;
        PostLikeDTO postLikeDTO = new PostLikeDTO(post.getId(), wrongUserId);

        // when // then
        assertThatThrownBy(() -> postLikeService.delete(postLikeDTO)).isInstanceOf(
            UserAccountNotFoundException.class);
    }


}