package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.domain.vo.Profile;

@Schema(description = "블로그 정보 DTO")
public record ProfileDTO(
    @Schema(description = "블로그 제목")
    @NotNull(message = "블로그 제목을 입력해주세요")
    String blogName,

    @Schema(description = "블로그 소개", nullable = true)
    String blogShortBio
) {

    public Profile toVO() {
        return Profile.of(blogName, blogShortBio);
    }

    public static ProfileDTO from(Profile profile) {
        return new ProfileDTO(profile.getBlogName(), profile.getBlogShortBio());
    }
}
