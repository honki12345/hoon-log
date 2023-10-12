package me.honki12345.hoonlog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;

import java.time.LocalDateTime;

@Schema(description = "회원 응답 DTO")
public record UserAccountResponse(
    @Schema(description = "회원 번호")
    Long id,
    @Schema(description = "아이디")
    String username,
    @Schema(description = "이메일")
    String email,
    @Schema(description = "블로그 정보")
    @JsonProperty("profile")
    ProfileDTO profileDTO,
    @Schema(description = "가입 일자")
    LocalDateTime createdAt,
    @Schema(description = "권한")
    Set<Role> roles
) {

    public static UserAccountResponse from(UserAccountDTO dto) {
        return new UserAccountResponse(dto.id(), dto.username(), dto.email(), dto.profileDTO(),
            dto.createdAt(), dto.roles());
    }
}
