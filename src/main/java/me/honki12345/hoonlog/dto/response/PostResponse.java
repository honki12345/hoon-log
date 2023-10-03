package me.honki12345.hoonlog.dto.response;

import java.time.LocalDateTime;
import me.honki12345.hoonlog.dto.PostDTO;

public record PostResponse(
    Long id,
    String title,
    String content,
    String createdBy,
    LocalDateTime createdAt
) {

    public static PostResponse from(PostDTO dto) {
        return new PostResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.createdBy(),
            dto.createdAt()
        );
    }

}
