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
import me.honki12345.hoonlog.domain.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional
@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final FileUtil fileUtil;

    @Transactional(readOnly = true)
    public List<PostImageDTO> searchPostImagesByPostId(Long postId) {
        return postImageRepository.findByPost_Id(postId).stream().map(PostImageDTO::from).collect(
            Collectors.toList());
    }

    public PostImageDTO savePostImageWithPost(MultipartFile postImageFile, Post post) {
        try {
            PostImage postImage = fileUtil.fromMultipartFileToPostImage(postImageFile);
            post.addPostImage(postImage);
            PostImage savedPostImage = postImageRepository.save(postImage);
            return PostImageDTO.from(savedPostImage);
        } catch (IOException e) {
            throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }

    }

    public List<PostImageDTO> savePostImagesWithPost(List<MultipartFile> multipartFiles,
        Post post) {
        return multipartFiles.stream().map(multipartFile -> savePostImageWithPost(multipartFile, post)).collect(
            Collectors.toList());
    }

    public void updatePostImage(Long postImageId, MultipartFile postImageFile) {
        if (postImageFile == null || postImageFile.isEmpty()) {
            return;
        }

        PostImage findPostImage = postImageRepository.findById(postImageId)
            .orElseThrow(() -> new ImageNotFoundException(
                ErrorCode.IMAGE_NOT_FOUND));

        if (!StringUtils.hasText(findPostImage.getImgName())) {
            fileUtil.deleteFile(FileUtil.IMAGE_LOCATION + File.separator + findPostImage.getImgName());

            try {
                String originalFilename = postImageFile.getOriginalFilename();
                String imageName = fileUtil.uploadFile(FileUtil.IMAGE_LOCATION, originalFilename,
                    postImageFile.getBytes());
                String imageUrl = FileUtil.UPLOAD_URL + imageName;
                findPostImage.updatePostImage(originalFilename, imageName, imageUrl);
            } catch (IOException e) {
                throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
            }
        }
    }
}
