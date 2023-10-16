package me.honki12345.hoonlog.domain.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@DisplayName("FileUtils 단위테스트")
class FileUtilsTest {

    @DisplayName("[삭제/실패]존재하지 않는 파일 삭제시, 로깅을 남긴다.")
    @Test
    void givenWrongFilePath_whenDeletingFile_thenLogging(CapturedOutput output) {
        // given // when
        FileUtils fileUtils = new FileUtils();
        fileUtils.deleteFile("wrongFilePath");

        // then
        assertThat(output.getOut().contains("파일이 존재하지 않습니다: wrongFilePath")).isTrue();
    }
}