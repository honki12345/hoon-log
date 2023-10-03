package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.dto.PostDTO;

public record PostRequest(
    @NotNull(message = "제목을 입력해주세요")
    String title,
    String content
) {

    public PostDTO toDTO() {
        return PostDTO.of(title, content);
    }
}
