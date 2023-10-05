package me.honki12345.hoonlog.service;

import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.ImageNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.ImageUploadFailException;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.util.FileUtil;
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

    public void savePostImage(PostImage postImage, MultipartFile postImageFile) {
        String originalFilename = postImageFile.getOriginalFilename();
        String imageName = "";
        String imageUrl = "";

        try {
            if (originalFilename != null && StringUtils.hasText(originalFilename)) {
                imageName = fileUtil.uploadFile(FileUtil.IMAGE_LOCATION, originalFilename,
                    postImageFile.getBytes());
                imageUrl = FileUtil.UPLOAD_URL + imageName;
            }
        } catch (IOException e) {
            throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }

        postImage.updatePostImage(originalFilename, imageName, imageUrl);
        postImageRepository.save(postImage);
    }

    public void updatePostImage(Long postImageId, MultipartFile postImageFile) {
        if (postImageFile != null && !postImageFile.isEmpty()) {
            PostImage savedImage = postImageRepository.findById(postImageId)
                .orElseThrow(() -> new ImageNotFoundException(
                    ErrorCode.IMAGE_NOT_FOUND));

            if (!StringUtils.hasText(savedImage.getImgName())) {
                fileUtil.deleteFile(FileUtil.IMAGE_LOCATION + File.separator + savedImage.getImgName());
            }

            try {
                String originalFilename = postImageFile.getOriginalFilename();
                String imageName = fileUtil.uploadFile(FileUtil.IMAGE_LOCATION, originalFilename,
                    postImageFile.getBytes());
                String imageUrl = FileUtil.UPLOAD_URL + imageName;
                savedImage.updatePostImage(originalFilename, imageName, imageUrl);
            } catch (IOException e) {
                throw new ImageUploadFailException(ErrorCode.IMAGE_UPLOAD_ERROR);
            }
        }
    }
}
