package me.honki12345.hoonlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.error.exception.domain.ImageNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.ImageUploadFailException;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.util.FileHelper;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import me.honki12345.hoonlog.util.PostBuilder;
import me.honki12345.hoonlog.util.UserAccountBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("PostImageService 애플리케이션 통합테스트")
class PostImageServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserAccountBuilder userAccountBuilder;
    @Autowired
    private PostBuilder postBuilder;
    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private PostImageService postImageService;
    @Autowired
    private PostImageRepository postImageRepository;

    @AfterEach
    void tearDown() {
        postBuilder.deleteAllInBatch();
        userAccountBuilder.deleteAllInBatch();
    }

    @DisplayName("[생성/실패]게시물 이미지 저장시, 이미지 파일 이름이 없으면, 예외를 던진다")
    @Test
    void givenNoNamedPostFileInfo_whenSavingPostImages_thenThrowsException() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        MultipartFile mockMultipartFile = fileHelper.createMockMultipartFile(null);

        // when // then
        assertThatThrownBy(
            () -> postImageService.savePostImageWithPost(mockMultipartFile,
                postByTestUser)).isInstanceOf(
            ImageUploadFailException.class);
    }


    @DisplayName("[생성/성공]여러개의 이미지파일 저장에 성공한다")
    @Test
    void givenSavingPostImageInfos_whenSavingPostImages_thenReturnsSavedPostImageInfos() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        List<MultipartFile> mockMultipartFiles = fileHelper.createMockMultipartFiles();

        // when
        List<PostImageDTO> postImageDTOList = postImageService.savePostImagesWithPost(
            mockMultipartFiles, postByTestUser);

        // then
        assertThat(mockMultipartFiles.size()).isEqualTo(postImageDTOList.size());
        for (int i = 0; i < mockMultipartFiles.size(); i++) {
            assertThat(mockMultipartFiles.get(i).getOriginalFilename()).isEqualTo(
                postImageDTOList.get(i).originalImgName());
        }
    }


    @DisplayName("[수정/성공]게시글 이미지파일 수정에 성공한다")
    @Test
    void givenUpdatePostImageInfo_whenUpdatingPostImage_thenReturnsUpdatedPostImageInfo() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        List<MultipartFile> mockMultipartFiles = fileHelper.createMockMultipartFiles();
        PostImageDTO postImageDTO = postImageService.savePostImageWithPost(
            mockMultipartFiles.get(0), postByTestUser);

        // when
        PostImageDTO updatedPostImageDTO = postImageService.updatePostImage(postImageDTO.id(),
            mockMultipartFiles.get(1));
        PostImage postImage = postImageRepository.findById(postImageDTO.id()).orElseThrow();

        // then
        assertThat(postImage.getId()).isEqualTo(updatedPostImageDTO.id());
        assertThat(postImage.getImgName()).isEqualTo(updatedPostImageDTO.imgName());
        assertThat(postImage.getImgUrl()).isEqualTo(updatedPostImageDTO.imgUrl());
        assertThat(postImage.getOriginalImgName()).isEqualTo(updatedPostImageDTO.originalImgName());
    }

    @DisplayName("[수정/실패]이미지파일 수정시, 저장된 이미지파일 번호가 올바르지 않으면, 예외를 던진다")
    @Test
    void givenSavedPostIdIsWrong_whenUpdatingPostImage_thenThrowsException() {
        // given
        userAccountBuilder.saveTestUser();
        Long wrongPostId = 999L;
        MultipartFile updateMultipartFile = fileHelper.createMockMultipartFile("after");

        // when // then
        assertThatThrownBy(() -> postImageService.updatePostImage(wrongPostId,
            updateMultipartFile)).isInstanceOf(ImageNotFoundException.class);
    }

    @DisplayName("[수정/실패]이미지파일 수정시, 이미지파일이름이 올바르지 않으면, 예외를 던진다")
    @Test
    void givenUpdatePostImageNamedNull_whenUpdatingPostImage_thenThrowsException() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        MultipartFile beforeMultipartFile = fileHelper.createMockMultipartFile("before.jpg");
        PostImageDTO savedPostImageDTO = postImageService.savePostImageWithPost(
            beforeMultipartFile, postByTestUser);
        MultipartFile updateMultipartFile = fileHelper.createMockMultipartFile("after");

        // when // then
        assertThatThrownBy(() -> postImageService.updatePostImage(savedPostImageDTO.id(),
            updateMultipartFile)).isInstanceOf(ImageUploadFailException.class);
    }

    @DisplayName("[수정/실패]이미지파일 수정시, 이미지파일이 null이면, 예외를 던진다")
    @Test
    void givenUpdatePostImageIsNull_whenUpdatingPostImage_thenThrowsException() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        MultipartFile beforeMultipartFile = fileHelper.createMockMultipartFile("before.jpg");
        PostImageDTO savedPostImageDTO = postImageService.savePostImageWithPost(
            beforeMultipartFile, postByTestUser);

        // when // then
        assertThatThrownBy(() -> postImageService.updatePostImage(savedPostImageDTO.id(),
            null)).isInstanceOf(ImageUploadFailException.class);
    }

    @DisplayName("[수정/실패]이미지파일 수정시, 이미지파일이 빈 파일이면, 예외를 던진다")
    @Test
    void givenPostImageIsEmpty_whenUpdatingPostImage_thenThrowsException() {
        // given
        userAccountBuilder.saveTestUser();
        Post postByTestUser = postBuilder.createPostByTestUser();
        MultipartFile beforeMultipartFile = fileHelper.createMockMultipartFile("before.jpg");
        PostImageDTO savedPostImageDTO = postImageService.savePostImageWithPost(
            beforeMultipartFile, postByTestUser);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("empty.jpg", new byte[]{});

        // when // then
        assertThatThrownBy(() -> postImageService.updatePostImage(savedPostImageDTO.id(),
            mockMultipartFile)).isInstanceOf(ImageUploadFailException.class);
    }


}