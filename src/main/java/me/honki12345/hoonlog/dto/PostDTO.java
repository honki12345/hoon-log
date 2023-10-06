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
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy(),
            entity.getPostImages().stream().map(PostImage::getId).collect(Collectors.toList()),
            entity.getTags().stream().map(Tag::getId).collect(Collectors.toUnmodifiableSet())
        );
    }

    public static PostDTO of(String title, String content, List<Long> postImageIds) {
        return PostDTO.of(title, content, postImageIds, null);
    }

    public static PostDTO of(String title, String content, List<Long> postImageIds,
        Set<Long> tagIds) {
        return new PostDTO(null, null, title, content, null, null, null, null,  postImageIds,
            tagIds);
    }

    public Post toEntity() {
        return Post.of(id, title, content);
    }

    public PostDTO addPostImageDTOs(List<PostImageDTO> dtos) {
        return new PostDTO(this.id, this.userId, this.title, this.content, this.createdAt,
            this.createdBy, this.modifiedAt, this.modifiedBy, this.postImageIds,
            this.tagIds);
    }
}
