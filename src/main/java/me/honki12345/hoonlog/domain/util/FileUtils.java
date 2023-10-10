package me.honki12345.hoonlog.domain.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import me.honki12345.hoonlog.domain.PostImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component("fileUtils")
public class FileUtils {

    public static String UPLOAD_LOCATION;
    public static String UPLOAD_URL;
    public static String IMAGE_LOCATION;

    @Value("${file.upload.location}")
    public void setUploadLocation(String uploadLocation) {
        UPLOAD_LOCATION = uploadLocation;
    }

    @Value("${file.upload.url}")
    public void setUploadUrl(String uploadUrl) {
        UPLOAD_URL = uploadUrl;
    }

    @Value("${file.image.location}")
    public void setImageLocation(String imageLocation) {
        IMAGE_LOCATION = imageLocation;
    }

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData)
        throws IOException {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String fileUploadFullUrl = uploadPath + File.separator + savedFileName;
        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(fileData);
        }
        return savedFileName;
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);

        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일 삭제에 성공했습니다: {}", filePath);
        } else {
            log.info("파일이 존재하지 않습니다: {}", filePath);
        }
    }

    public PostImage fromMultipartFileToPostImage(MultipartFile postImageFile) throws IOException {
        String originalFilename = postImageFile.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IOException();
        }
        String imageName = uploadFile(FileUtils.IMAGE_LOCATION,
            originalFilename,
            postImageFile.getBytes());
        String imageUrl = FileUtils.UPLOAD_URL + imageName;

        return PostImage.of(originalFilename, imageName, imageUrl);
    }

}
