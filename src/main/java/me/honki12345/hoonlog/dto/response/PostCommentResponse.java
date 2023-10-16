package me.honki12345.hoonlog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import me.honki12345.hoonlog.dto.PostCommentDTO;

@Schema(description = "댓글 응답 DTO")
public record PostCommentResponse(
    @Schema(description = "댓글 번호")
    Long id,
    @Schema(description = "댓글 내용")
    String content,
    @Schema(description = "댓글 작성일자")
    LocalDateTime createdAt,
    @Schema(description = "댓글 작성자")
    String createdBy,
    @Schema(description = "댓글 수정일자")
    LocalDateTime modifiedAt
) {

    public static PostCommentResponse from(PostCommentDTO dto) {
        return new PostCommentResponse(dto.id(), dto.content(), dto.createdAt(), dto.createdBy(),
            dto.modifiedAt());
    }
}
