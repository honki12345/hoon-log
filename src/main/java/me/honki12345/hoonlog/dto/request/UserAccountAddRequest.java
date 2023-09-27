package me.honki12345.hoonlog.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.ProfileDTO;

public record UserAccountAddRequest(
        @NotNull(message = "아이디를 입력해주세요")
        String userId,

        @Size(min = 8, max = 20, message = "비밀번호 형식이 올바르지 않습니다")
        String userPassword,

        @Pattern(regexp = ".+@.+", message = "이메일 형식이 올바르지 않습니다")
        String email,

        @Valid
        @NotNull(message = "블로그 제목을 입력해주세요")
        ProfileDTO profile
) {

    public UserAccount toEntity(String encodedPwd) {
        return UserAccount.of(userId, encodedPwd, email, ProfileDTO.toVO(profile));
    }
}
