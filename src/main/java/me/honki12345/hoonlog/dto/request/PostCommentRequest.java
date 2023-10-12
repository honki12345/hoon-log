package me.honki12345.hoonlog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.dto.PostCommentDTO;

@Schema(description = "댓글 요청 DTO")
public record PostCommentRequest(
    @Schema(description = "게시글 번호")
    @NotNull(message = "잘못된 입력입니다")
    Long postId,

    @Schema(description = "댓글 내용")
    @NotNull(message = "내용을 입력해주세요")
    String content
) {

    public static PostCommentRequest of(String content) {
        return new PostCommentRequest(null, content);
    }

    public PostCommentDTO toDTO() {
        return PostCommentDTO.of(content);
    }
}
