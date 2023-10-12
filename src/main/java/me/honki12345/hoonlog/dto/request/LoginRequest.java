package me.honki12345.hoonlog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.honki12345.hoonlog.dto.UserAccountDTO;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
    @Schema(description = "아이디")
    @NotNull(message = "아이디를 입력해주세요")
    String username,

    @Schema(description = "비밀번호", minLength = 8, maxLength = 20)
    @Size(min = 8, max = 20, message = "비밀번호 형식이 올바르지 않습니다")
    String password
) {

    public UserAccountDTO toDTO() {
        return UserAccountDTO.of(username, password);
    }
}
