package me.honki12345.hoonlog.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.PostImageDTO;

public record PostResponse(
    Long id,
    String title,
    String content,
    String createdBy,
    LocalDateTime createdAt,
    List<PostImageDTO> postImageDTOs
) {

    public static PostResponse from(PostDTO dto) {
        return new PostResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.createdBy(),
            dto.createdAt(),
            dto.postImageDTOs()
        );
    }

}
