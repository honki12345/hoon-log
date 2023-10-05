package me.honki12345.hoonlog.dto;

import java.util.List;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.PostImage;

public record PostImageDTO(
    Long id,
    String imgName,
    String originalImgName,
    String imgUrl
) {

    public static PostImageDTO from(PostImage postImage) {
        return new PostImageDTO(postImage.getId(), postImage.getImgName(),
            postImage.getOriginalImgName(), postImage.getImgUrl());
    }

    public static List<PostImageDTO> from(List<PostImage> postImages) {
        return postImages.stream().map(PostImageDTO::from).collect(Collectors.toList());
    }
}
