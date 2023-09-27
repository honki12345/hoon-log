package me.honki12345.hoonlog.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.dto.ProfileDTO;

public record UserAccountModifyRequest(
        @Valid
        @NotNull(message = "블로그 제목을 입력해주세요")
        ProfileDTO profile
) {
}
