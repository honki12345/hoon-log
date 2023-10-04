package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.honki12345.hoonlog.dto.PostDTO;

public record PostRequest(
    @NotNull(message = "제목을 입력해주세요")
    String title,
    String content,
    List<Long> postImageIds
) {

    public PostRequest {
        if (Objects.isNull(postImageIds)) {
            postImageIds = new ArrayList<>();
        }
    }

    public PostDTO toDTO() {
        return PostDTO.of(title, content, postImageIds);
    }
}
