package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import me.honki12345.hoonlog.dto.PostDTO;

public record PostRequest(
    @NotNull(message = "제목을 입력해주세요")
    String title,
    String content,
    List<Long> postImageIds,
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

    public static PostRequest of(String title, String content, List<Long> postImageIds, Set<String> tagNames) {
        return new PostRequest(title, content, postImageIds, tagNames);
    }
    public PostDTO toDTO() {
        return PostDTO.of(title, content, postImageIds);
    }

}
