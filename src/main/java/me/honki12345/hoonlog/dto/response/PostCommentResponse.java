package me.honki12345.hoonlog.dto.response;

import java.time.LocalDateTime;
import me.honki12345.hoonlog.dto.PostCommentDTO;

public record PostCommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt

) {

    public static PostCommentResponse from(PostCommentDTO dto) {
        return new PostCommentResponse(dto.id(), dto.content(), dto.createdAt(), dto.createdBy(),
            dto.modifiedAt());
    }
}
