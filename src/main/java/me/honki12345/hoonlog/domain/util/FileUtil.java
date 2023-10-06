package me.honki12345.hoonlog.domain.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import me.honki12345.hoonlog.domain.PostImage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileUtil {

    public static final String UPLOAD_LOCATION =
        System.getProperty("user.home") + File.separator + "hoonlog";
    public static final String UPLOAD_URL = "/images/post/";
    public static final String IMAGE_LOCATION =
        System.getProperty("user.home") + File.separator + "hoonlog"
            + File.separator + "post";

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
        String imageName = uploadFile(FileUtil.IMAGE_LOCATION,
            originalFilename,
            postImageFile.getBytes());
        String imageUrl = FileUtil.UPLOAD_URL + imageName;

        return PostImage.of(originalFilename, imageName, imageUrl);
    }

}
