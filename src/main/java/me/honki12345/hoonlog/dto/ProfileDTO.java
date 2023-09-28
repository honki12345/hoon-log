package me.honki12345.hoonlog.dto;

import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.domain.vo.Profile;

public record ProfileDTO(
    @NotNull(message = "블로그 제목을 입력해주세요")
    String blogName,
    String blogShortBio
) {

    public static Profile toVO(ProfileDTO dto) {
        return Profile.of(dto.blogName(), dto.blogShortBio());
    }

    public static ProfileDTO from(Profile profile) {
        return new ProfileDTO(profile.getBlogName(), profile.getBlogShortBio());
    }
}
