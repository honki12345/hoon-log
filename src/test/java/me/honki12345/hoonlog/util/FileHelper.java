package me.honki12345.hoonlog.util;

import static me.honki12345.hoonlog.domain.util.FileUtils.UPLOAD_URL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@TestComponent
@RequiredArgsConstructor
public class FileHelper {

    public static final String TEST_FILE_ORIGINAL_NAME = "drawing.jpg";

    public String createImageFilePath(String fileName) {
        StringJoiner sj = new StringJoiner(File.separator);
        String pathname = System.getProperty("user.dir") + File.separator + "src";
        return sj.add(pathname).add("test").add("data").add(fileName)
            .toString();
    }

    public MultipartFile createMockMultipartFile(String imageFileName) {

        String path = UPLOAD_URL + File.separator;

        return new MockMultipartFile(path, imageFileName,
            "image/jpg", new byte[]{1, 2, 3, 4});
    }

    public List<MultipartFile> createMockMultipartFiles() {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = UPLOAD_URL + File.separator;
            String imageName = "image" + i + ".jpg";
            MockMultipartFile mockMultipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(mockMultipartFile);
        }

        return multipartFileList;
    }
}
