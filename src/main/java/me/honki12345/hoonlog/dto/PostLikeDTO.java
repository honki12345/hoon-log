package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시물 좋아요 DTO")
public record PostLikeDTO(
    @Schema(description = "(좋아요 대상)게시물 번호")
    Long postId,
    @Schema(description = "(좋아요를 요청한)유저 번호")
    Long userId
) {

}
