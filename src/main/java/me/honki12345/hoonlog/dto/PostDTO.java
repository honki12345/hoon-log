package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.Tag;

@Schema(description = "게시물 DTO")
public record PostDTO(
    @Schema(description = "게시물 번호")
    Long id,
    @Schema(description = "작성자 유저 번호")
    Long userId,
    @Schema(description = "게시물 제목")
    String title,
    @Schema(description = "게시물 내용")
    String content,
    @Schema(description = "게시물 좋아요 개수")
    Long likeCount,
    @Schema(description = "게시물 작성일자")
    LocalDateTime createdAt,
    @Schema(description = "게시물 작성자")
    String createdBy,
    @Schema(description = "게시물 수정일자")
    LocalDateTime modifiedAt,
    @Schema(description = "게시물 수정자")
    String modifiedBy,
    @Schema(description = "게시물 이미지 아이디들")
    List<Long> postImageIds,
    @Schema(description = "게시물 태그 아이디들")
    Set<Long> tagIds
) {

    public static PostDTO from(Post entity) {
        return new PostDTO(
            entity.getId(),
            entity.getUserAccount().getId(),
            entity.getTitle(),
            entity.getContent(),
            entity.getLikeCount(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy(),
            entity.getPostImages().stream().map(PostImage::getId).collect(Collectors.toList()),
            entity.getTags().stream().map(Tag::getId).collect(Collectors.toUnmodifiableSet())
        );
    }

    public static PostDTO of(String title, String content, List<Long> postImageIds) {
        return new PostDTO(null, null, title, content, null, null, null, null, null, postImageIds,
            null);
    }

    public Post toEntity() {
        return Post.of(id, title, content);
    }
}
