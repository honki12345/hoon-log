package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;

@Schema(description = "게시물 이미지 DTO")
public record PostImageDTO(
    @Schema(description = "게시물 이미지 번호")
    Long id,
    @Schema(description = "게시물 이미지 이름")
    String imgName,
    @Schema(description = "(업로드 요청시 파일이름) 게시물 이미지 원본 이름")
    String originalImgName,
    @Schema(description = "게시물 이미지 URL")
    String imgUrl,
    @Schema(description = "(게시물 이미지가 속한 게시물) 게시물 번호")
    Long postId
) {

    public static PostImageDTO from(PostImage postImage) {
        return new PostImageDTO(postImage.getId(), postImage.getImgName(),
            postImage.getOriginalImgName(), postImage.getImgUrl(), postImage.getPost().getId());
    }


    public static List<PostImageDTO> from(List<PostImage> postImages) {
        return postImages.stream().map(PostImageDTO::from).collect(Collectors.toList());
    }
}
