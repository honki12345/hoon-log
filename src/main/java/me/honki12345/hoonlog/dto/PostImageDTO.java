package me.honki12345.hoonlog.dto;

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
}
