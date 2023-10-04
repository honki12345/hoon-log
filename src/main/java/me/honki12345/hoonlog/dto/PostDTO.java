package me.honki12345.hoonlog.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;

public record PostDTO(
    Long id,
    UserAccountDTO userAccountDTO,
    String title,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    List<PostImageDTO> postImageDTOList,
    List<Long> postImageIds

) {

    public PostDTO {
    }

    public static PostDTO from(Post entity) {
        return new PostDTO(
            entity.getId(),
            UserAccountDTO.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy(),
            PostImageDTO.from(entity.getPostImages()),
            null
        );
    }

    public static PostDTO of(String title, String content, List<Long> postImageIds) {
        return new PostDTO(null, null, title, content, null, null, null, null, null, postImageIds);
    }

    public Post toEntity() {
        UserAccount userAccount = Optional.ofNullable(userAccountDTO).map(UserAccountDTO::toEntity)
            .orElse(null);
        return Post.of(id, userAccount, title, content);
    }

    public PostDTO addPostImageDTOList(List<PostImageDTO> dtos) {
        return new PostDTO(this.id, this.userAccountDTO, this.title, this.content, this.createdAt,
            this.createdBy, this.modifiedAt, this.modifiedBy, dtos, this.postImageIds);
    }
}
