package me.honki12345.hoonlog.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.ImageNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.ImageUploadFailException;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.domain.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional
@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final FileUtils fileUtils;

    public PostImageDTO savePostImageWithPost(MultipartFile postImageFile, Post post) {
        try {
            PostImage postImage = fileUtils.fromMultipartFileToPostImage(postImageFile);
            post.addPostImage(postImage);
            PostImage savedPostImage = postImageRepository.save(postImage);
            return PostImageDTO.from(savedPostImage);
        } catch (IOException e) {
            throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }

    }

    public List<PostImageDTO> savePostImagesWithPost(List<MultipartFile> multipartFiles,
        Post post) {
        return multipartFiles.stream()
            .map(multipartFile -> savePostImageWithPost(multipartFile, post)).collect(
                Collectors.toList());
    }

    public PostImageDTO updatePostImage(Long postImageId, MultipartFile postImageFile) {

        PostImage findPostImage = postImageRepository.findById(postImageId)
            .orElseThrow(() -> new ImageNotFoundException(
                ErrorCode.IMAGE_NOT_FOUND));

        fileUtils.deleteFile(
            FileUtils.IMAGE_LOCATION + File.separator + findPostImage.getImgName());

        if (postImageFile == null || postImageFile.isEmpty()) {
            throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }

        try {
            String originalFilename = postImageFile.getOriginalFilename();
            String imageName = fileUtils.uploadFile(FileUtils.IMAGE_LOCATION, originalFilename,
                postImageFile.getBytes());
            String imageUrl = FileUtils.UPLOAD_URL + imageName;
            findPostImage.updatePostImage(originalFilename, imageName, imageUrl);
            return PostImageDTO.from(findPostImage);
        } catch (Exception e) {
            throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }
    }
}
