package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import me.honki12345.hoonlog.domain.Post;

public record PostDTO(
    Long id,
    UserAccountDTO userAccountDTO,
    String title,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {

    public static PostDTO from(Post entity) {
        return new PostDTO(
            entity.getId(),
            UserAccountDTO.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public static PostDTO of(UserAccountDTO userAccountDTO, String title, String content) {
        return new PostDTO(null, userAccountDTO, title, content, null, null, null, null);
    }

}
