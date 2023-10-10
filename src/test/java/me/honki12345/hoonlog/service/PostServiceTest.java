package me.honki12345.hoonlog.service;

import static me.honki12345.hoonlog.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.config.TestJpaConfig;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.util.FileUtils;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.exception.domain.DeletePostForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UpdatePostForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostImageRepository;
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
import org.springframework.web.multipart.MultipartFile;

@DisplayName("PostService 애플리케이션 통합테스트")
@Import({TestUtils.class, TestJpaConfig.class})
@ActiveProfiles("test")
@SpringBootTest
class PostServiceTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;
    @Autowired
    FileUtils fileUtils;

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    PostService postService;

    @AfterEach
    void tearDown() {
        testUtils.deleteAllInBatchInAllRepository();
    }

    @DisplayName("[저장/성공]게시글 생성에 성공한다.")
    @Test
    void givenPostInfoAndUserInfo_whenAddingPost_thenReturnsSavedPostInfo() {
        // given
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(TEST_USERNAME, TEST_PASSWORD);
        UserAccountPrincipal userAccountPrincipal = UserAccountPrincipal.from(userAccountDTO);

        // when
        Post post = postService.addPost(postRequest.toDTO(), null,
            postRequest.tagNames(), userAccountPrincipal.toDTO());

        // then
        assertThat(post.getTitle()).isEqualTo(TEST_POST_TITLE);
        assertThat(post.getContent()).isEqualTo(TEST_POST_CONTENT);
    }

    @DisplayName("[저장/성공]파일을 첨부하여 게시글 생성에 성공한다.")
    @Test
    void givenPostInfoWithImageFile_whenAddingPost_thenReturnsSavedPostInfo() {
        // given
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(TEST_USERNAME, TEST_PASSWORD);
        UserAccountPrincipal userAccountPrincipal = UserAccountPrincipal.from(userAccountDTO);
        List<MultipartFile> multipartFiles = testUtils.createMockMultipartFiles();

        // when
        Post post = postService.addPost(postRequest.toDTO(), multipartFiles,
            postRequest.tagNames(), userAccountPrincipal.toDTO());
        List<PostImage> postImages = postImageRepository.findByPost_Id(post.getId());
        Post searchedPost = postService.searchPost(post.getId());

        // then
        assertThat(searchedPost.getTitle()).isEqualTo(TEST_POST_TITLE);
        assertThat(searchedPost.getContent()).isEqualTo(TEST_POST_CONTENT);
        assertThat(multipartFiles.get(0).getOriginalFilename()).isEqualTo(
            postImages.get(0).getOriginalImgName());
    }


    @DisplayName("[저장/실패]회원가입 되지 않은 회원정보로, 게시글 생성시, 예외를 던진다.")
    @Test
    void givenPostInfoWithUnRegisteredUserInfo_whenAddingPost_thenThrowsException() {
        // given
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        long wrongUserId = 3L;
        UserAccountPrincipal userAccountPrincipal = new UserAccountPrincipal(wrongUserId,
            TEST_USERNAME, List.of("USER_ROLE"));

        // when // then
        assertThatThrownBy(() ->
            postService.addPost(postRequest.toDTO(), null,
                postRequest.tagNames(), userAccountPrincipal.toDTO())).isInstanceOf(
            UserAccountNotFoundException.class);
    }

    @DisplayName("[수정/성공]게시글 수정에 성공한다.")
    @Test
    void givenUpdatingPostInfoWithUnRegisteredUserInfo_whenUpdatingPost_thenReturnsUpdatedPostInfo() {
        // given

        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post savedPost = testUtils.createPostByTestUser("title", "content");
        String newTitle = "newTitle";
        String newContent = "newContent";
        PostRequest updateRequest = PostRequest.of(newTitle, newContent, Set.of(TEST_TAG_NAME));

        // when
        PostDTO updatedPostDTO = PostDTO.from(
            postService.updatePost(savedPost.getId(),
                userAccountDTO,
                updateRequest.toDTO(), null, updateRequest.tagNames()));

        // then
        assertThat(updatedPostDTO.id()).isEqualTo(savedPost.getId());
        assertThat(updatedPostDTO.createdBy()).isEqualTo(savedPost.getCreatedBy());
        assertThat(updatedPostDTO.title()).isEqualTo(newTitle);
        assertThat(updatedPostDTO.content()).isEqualTo(newContent);
        assertThat(updatedPostDTO.tagIds()).isNotNull();
    }

    @DisplayName("[수정/성공](첨부파일)게시글 수정에 성공한다.")
    @Test
    void givenUpdatingPostInfoWithPostImages_whenUpdatingPost_thenReturnsUpdatedPostInfo()
        throws IOException {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        PostImage postImage = fileUtils.fromMultipartFileToPostImage(
            testUtils.createMockMultipartFile("mock.jpg"));
        Post savedPost = testUtils.createPostWithImageFileByTestUser(
            postImage);
        postRepository.save(savedPost);
        postImageRepository.save(postImage);

        PostRequest updateRequest = PostRequest.of(
            TEST_UPDATED_POST_TITLE,
            TEST_UPDATED_POST_CONTENT,
            savedPost.getPostImages().stream().map(PostImage::getId).collect(Collectors.toList()),
            Set.of(TEST_TAG_NAME));
        String updateFileName = "afterMock.jpg";
        MultipartFile updateMockMultipartFile = testUtils.createMockMultipartFile(updateFileName);

        // when
        PostDTO updatedPostDTO = PostDTO.from(
            postService.updatePost(savedPost.getId(),
                userAccountDTO,
                updateRequest.toDTO(), List.of(updateMockMultipartFile), updateRequest.tagNames()));
        Post updatedPost = postService.searchPost(updatedPostDTO.id());

        // then
        assertThat(updatedPost.getId()).isEqualTo(savedPost.getId());
        assertThat(updatedPost.getCreatedBy()).isEqualTo(savedPost.getCreatedBy());
        assertThat(updatedPost.getTitle()).isEqualTo(TEST_UPDATED_POST_TITLE);
        assertThat(updatedPost.getContent()).isEqualTo(TEST_UPDATED_POST_CONTENT);
        assertThat(updatedPost.getPostImages().get(0).getOriginalImgName()).isEqualTo(
            updateFileName);
        assertThat(updatedPostDTO.tagIds()).isNotNull();
    }

    @DisplayName("[수정/실패]저장되지 않은 게시물번호로, 게시글 수정시, 예외를 던진다.")
    @Test
    void givenUpdatingPostInfoWithUnsavedPostId_whenUpdatingPost_thenThrowsException() {
        // given

        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        testUtils.createPostByTestUser("title", "content");
        String newTitle = "newTitle";
        String newContent = "newContent";
        PostRequest updateRequest = PostRequest.of(newTitle, newContent, Set.of(TEST_TAG_NAME));
        long unsavedPostId = 999L;

        // when // then
        assertThatThrownBy(() ->
            postService.updatePost(unsavedPostId,
                userAccountDTO,
                updateRequest.toDTO(),
                null,
                updateRequest.tagNames()))
            .isInstanceOf(PostNotFoundException.class);
    }

    @DisplayName("[수정/실패]작성하지 않은 유저가 요청시, 게시글 수정하면, 예외를 던진다.")
    @Test
    void givenUpdatingWithoutRegisteredUserInfo_whenUpdatingPost_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = UserAccountDTO.of("unsavedUser", "pwd");
        testUtils.saveTestUser();
        Post savedPost = testUtils.createPostByTestUser("title", "content");
        PostRequest updateRequest = PostRequest.of("newTitle", "newContent", Set.of(TEST_TAG_NAME));

        // when // then
        assertThatThrownBy(() ->
            postService.updatePost(savedPost.getId(),
                userAccountDTO,
                updateRequest.toDTO(), null, updateRequest.tagNames())
        ).isInstanceOf(UpdatePostForbiddenException.class);
    }


    @DisplayName("[삭제/성공]게시글 삭제에 성공한다.")
    @Test
    void givenPostIdWithUnRegisteredUserInfo_whenDeletingPost_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post savedPost = testUtils.createPostByTestUser("title", "content");

        // when // then
        assertThatNoException().isThrownBy(
            () -> postService.deletePost(savedPost.getId(), userAccountDTO));
    }

    @DisplayName("[삭제/실패]작성하지 않은 유저가 요청하면, 게시글 삭제시, 예외를 던진다.")
    @Test
    void givenPostIdWithoutRegisteredUserInfo_whenDeletingPost_thenThrowsException() {
        // given
        UserAccountDTO userAccountDTO = UserAccountDTO.of("unsavedUser", "pwd");
        testUtils.saveTestUser();
        Post savedPost = testUtils.createPostByTestUser("title", "content");

        // when // then
        assertThatThrownBy(
            () -> postService.deletePost(savedPost.getId(), userAccountDTO)).isInstanceOf(
            DeletePostForbiddenException.class);
    }

    @DisplayName("[삭제/실패]저장되지 않은 게시물로 요청하면, 게시글 삭제시, 예외를 던진다.")
    @Test
    void givenPostIdWithUnsavedPostId_whenDeletingPost_thenThrowsException() {
        // given
        Long unsavedPostId = 999L;
        UserAccountDTO userAccountDTO = UserAccountDTO.of("unsavedUser", "pwd");
        testUtils.saveTestUser();
        testUtils.createPostByTestUser("title", "content");

        // when // then
        assertThatThrownBy(
            () -> postService.deletePost(unsavedPostId, userAccountDTO)).isInstanceOf(
            PostNotFoundException.class);
    }
}