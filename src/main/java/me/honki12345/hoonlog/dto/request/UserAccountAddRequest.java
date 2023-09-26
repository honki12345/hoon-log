package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.honki12345.hoonlog.domain.UserAccount;

public record UserAccountAddRequest(
        @NotNull(message = "아이디를 입력해주세요")
        String userId,

        @Size(min = 8, max = 20, message = "비밀번호 형식이 올바르지 않습니다")
        String userPassword,

        @Pattern(regexp = ".+@.+", message = "이메일 형식이 올바르지 않습니다")
        String email
) {

    public UserAccount toEntity(String encodedPwd) {
        return UserAccount.of(userId, encodedPwd, email);
    }
}
