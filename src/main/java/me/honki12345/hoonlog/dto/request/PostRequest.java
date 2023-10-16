package me.honki12345.hoonlog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import me.honki12345.hoonlog.dto.PostDTO;

@Schema(description = "게시글 요청 DTO")
public record PostRequest(
    @Schema(description = "게시글 제목")
    @NotNull(message = "제목을 입력해주세요")
    String title,

    @Schema(description = "게시글 내용", nullable = true)
    String content,

    @Schema(description = "게시글 이미지 번호들", nullable = true)
    List<Long> postImageIds,

    @Schema(description = "게시글 태그 이름들", nullable = true)
    Set<String> tagNames
) {

    public PostRequest {
        if (Objects.isNull(postImageIds)) {
            postImageIds = new ArrayList<>();
        }

        if (Objects.isNull(tagNames)) {
            tagNames = new LinkedHashSet<>();
        }
    }

    public static PostRequest of(String title, String content) {
        return new PostRequest(title, content, null, null);
    }

    public static PostRequest of(String title, String content, Set<String> tagNames) {
        return new PostRequest(title, content, null, tagNames);
    }

    public static PostRequest of(String title, String content, List<Long> postImageIds,
        Set<String> tagNames) {
        return new PostRequest(title, content, postImageIds, tagNames);
    }

    public PostDTO toDTO() {
        return PostDTO.of(title, content, postImageIds);
    }

}
