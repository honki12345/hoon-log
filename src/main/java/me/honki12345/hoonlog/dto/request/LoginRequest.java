package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotNull(message = "아이디를 입력해주세요")
    String username,

    @Size(min = 8, max = 20, message = "비밀번호 형식이 올바르지 않습니다")
    String password
) {

}
