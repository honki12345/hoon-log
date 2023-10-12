package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.PostComment;

@Schema(description = "댓글 DTO")
public record PostCommentDTO(
    @Schema(description = "댓글 번호")
    Long id,
    @Schema(description = "게시물 번호")
    Long postId,
    @Schema(description = "유저 번호")
    Long userId,
    @Schema(description = "댓글 내용")
    String content,
    @Schema(description = "댓글 작성일자")
    LocalDateTime createdAt,
    @Schema(description = "댓글 작성자")
    String createdBy,
    @Schema(description = "댓글 수정일자")
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
