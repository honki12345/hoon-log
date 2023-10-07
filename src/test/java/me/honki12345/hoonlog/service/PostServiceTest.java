package me.honki12345.hoonlog.service;

import static me.honki12345.hoonlog.util.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.domain.util.FileUtil;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("PostService 애플리케이션 통합테스트")
@Import({TestUtils.class})
@ActiveProfiles("test")
@SpringBootTest
class PostServiceTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;

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

    @DisplayName("게시글 생성에 성공한다.")
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

    @DisplayName("파일을 첨부하여 게시글 생성에 성공한다.")
    @Test
    void givenPostInfoWithImageFile_whenAddingPost_thenReturnsSavedPostInfo() {
        // given
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        UserAccountDTO userAccountDTO = testUtils.saveTestUser(TEST_USERNAME, TEST_PASSWORD);
        UserAccountPrincipal userAccountPrincipal = UserAccountPrincipal.from(userAccountDTO);
        List<MultipartFile> multipartFiles = createMultipartFiles();

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


    @DisplayName("회원가입 되지 않은 회원정보로, 게시글 생성시, 예외를 던진다.")
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

    @DisplayName("게시글 수정에 성공한다.")
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

    @DisplayName("게시글 삭제에 성공한다.")
    @Test
    void givenPostIdWithUnRegisteredUserInfo_whenDeletingPost_thenReturnsNothing() {
        // given
        UserAccountDTO userAccountDTO = testUtils.saveTestUser();
        Post savedPost = testUtils.createPostByTestUser("title", "content");

        // when // then
        assertThatNoException().isThrownBy(
            () -> postService.deletePost(savedPost.getId(), userAccountDTO));
    }

    List<MultipartFile> createMultipartFiles() {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = FileUtil.UPLOAD_URL + File.separator;
            String imageName = "image" + i + ".jpg";
            MockMultipartFile mockMultipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(mockMultipartFile);
        }

        return multipartFileList;
    }

}