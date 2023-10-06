package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.PostComment;

public record PostCommentDTO(
    Long id,
    Long postId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt
) {

    public static PostCommentDTO of(String content) {
        return new PostCommentDTO(null, null, null, content, null, null, null);
    }

    public static PostCommentDTO from(PostComment entity) {
        return new PostCommentDTO(entity.getId(), entity.getPost().getId(),
            entity.getUserAccount().getId(),
            entity.getContent(), entity.getCreatedAt(), entity.getCreatedBy(),
            entity.getModifiedAt());
    }

    public static Set<PostCommentDTO> from(Set<PostComment> postComments) {
        return postComments.stream().map(PostCommentDTO::from)
            .collect(Collectors.toUnmodifiableSet());
    }

    public PostComment toEntity() {
        return PostComment.of(id, content);
    }
}
