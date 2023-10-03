package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.dto.PostCommentDTO;

public record PostCommentRequest(
    @NotNull(message = "잘못된 입력입니다")
    Long postId,
    @NotNull(message = "내용을 입력해주세요")
    String content
) {

    public PostCommentDTO toDTO() {
        return PostCommentDTO.of(content);
    }
}
