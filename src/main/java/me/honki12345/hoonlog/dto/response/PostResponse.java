package me.honki12345.hoonlog.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.dto.TagDTO;

public record PostResponse(
    Long id,
    String title,
    String content,
    String createdBy,
    LocalDateTime createdAt,
    List<PostImageDTO> postImageDTOs,
    Set<PostCommentDTO> postCommentDTOs,
    Set<TagDTO> tagDTOs
) {

    public static PostResponse from(PostDTO postDTO, List<PostImageDTO> postImageDTOs,
        Set<PostCommentDTO> postCommentDTOs, Set<TagDTO> tagDTOs) {

        return new PostResponse(
            postDTO.id(),
            postDTO.title(),
            postDTO.content(),
            postDTO.createdBy(),
            postDTO.createdAt(),
            postImageDTOs,
            postCommentDTOs,
            tagDTOs
        );
    }

    public static PostResponse from(PostDTO post) {
        return new PostResponse(post.id(), post.title(), post.content(), post.createdBy(),
            post.createdAt(), null, null, null);
    }


    public static PostResponse from(Post post) {
        return from(PostDTO.from(post), PostImageDTO.from(post.getPostImages()),
            PostCommentDTO.from(post.getPostComments()), TagDTO.fromWithoutPostIds(post.getTags()));
    }

}
