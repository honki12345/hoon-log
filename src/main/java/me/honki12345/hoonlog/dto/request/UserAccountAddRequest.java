package me.honki12345.hoonlog.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;

public record UserAccountAddRequest(
    @NotNull(message = "아이디를 입력해주세요")
    String username,

    @Size(min = 8, max = 20, message = "비밀번호 형식이 올바르지 않습니다")
    String userPassword,

    @Pattern(regexp = ".+@.+", message = "이메일 형식이 올바르지 않습니다")
    String email,

    @Valid
    @NotNull(message = "블로그 제목을 입력해주세요")
    ProfileDTO profile
) {

    public UserAccountDTO toDTO() {
        return UserAccountDTO.of(null, username, userPassword, email, profile);
    }

    public UserAccount toEntity(String encodedPwd) {
        return UserAccount.of(username, encodedPwd, email, profile.toVO());
    }
}
