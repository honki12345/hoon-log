package me.honki12345.hoonlog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.dto.TagDTO;

@Schema(description = "게시물 응답 DTO")
public record PostResponse(
    @Schema(description = "게시물 번호")
    Long id,
    @Schema(description = "게시물 번호")
    String title,
    @Schema(description = "게시물 내용")
    String content,
    @Schema(description = "게시물 작성자")
    String createdBy,
    @Schema(description = "좋아요 개수")
    Long likeCount,
    @Schema(description = "게시물 작성일자")
    LocalDateTime createdAt,
    @Schema(description = "게시물 이미지들 정보")
    List<PostImageDTO> postImageDTOs,
    @Schema(description = "게시물 댓글들 정보")
    Set<PostCommentDTO> postCommentDTOs,
    @Schema(description = "게시물 태그들 정보")
    Set<TagDTO> tagDTOs
) {

    public static PostResponse from(PostDTO postDTO, List<PostImageDTO> postImageDTOs,
        Set<PostCommentDTO> postCommentDTOs, Set<TagDTO> tagDTOs) {

        return new PostResponse(
            postDTO.id(),
            postDTO.title(),
            postDTO.content(),
            postDTO.createdBy(),
            postDTO.likeCount(),
            postDTO.createdAt(),
            postImageDTOs,
            postCommentDTOs,
            tagDTOs
        );
    }

    public static PostResponse from(PostDTO post) {
        return new PostResponse(post.id(), post.title(), post.content(), post.createdBy(),
            post.likeCount(), post.createdAt(), null, null, null);
    }


    public static PostResponse from(Post post) {
        return from(PostDTO.from(post), PostImageDTO.from(post.getPostImages()),
            PostCommentDTO.from(post.getPostComments()), TagDTO.fromWithoutPostIds(post.getTags()));
    }

}
