package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.Tag;

public record PostDTO(
    Long id,
    Long userId,
    String title,
    String content,
    Long likeCount,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    List<Long> postImageIds,
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
