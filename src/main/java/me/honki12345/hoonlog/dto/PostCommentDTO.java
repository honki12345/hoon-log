package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.domain.UserAccount;

public record PostCommentDTO(
    Long id,
    PostDTO postDTO,
    UserAccountDTO userAccountDTO,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt
) {

    public static PostCommentDTO of(String content) {
        return new PostCommentDTO(null, null, null, content, null, null, null);
    }

    public static PostCommentDTO from(PostComment entity) {
        return new PostCommentDTO(entity.getId(), PostDTO.from(entity.getPost()),
            UserAccountDTO.from(entity.getUserAccount()),
            entity.getContent(), entity.getCreatedAt(), entity.getCreatedBy(),
            entity.getModifiedAt());
    }

    public PostComment toEntity() {
        Post post = Optional.ofNullable(postDTO).map(PostDTO::toEntity).orElse(null);
        UserAccount userAccount = Optional.ofNullable(userAccountDTO).map(UserAccountDTO::toEntity)
            .orElse(null);
        return PostComment.of(id, post, userAccount, content);
    }
}
