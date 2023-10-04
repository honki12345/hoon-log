package me.honki12345.hoonlog.dto;

import java.util.ArrayList;
import java.util.List;
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

    public static List<PostImageDTO> from(List<PostImage> postImageList) {
        List<PostImageDTO> postImageDTOList = new ArrayList<>();
        for (PostImage postImage : postImageList) {
            PostImageDTO postImageDTO = PostImageDTO.from(postImage);
            postImageDTOList.add(postImageDTO);
        }

        return postImageDTOList;
    }
}
