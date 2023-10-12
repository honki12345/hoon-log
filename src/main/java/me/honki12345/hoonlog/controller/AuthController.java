package me.honki12345.hoonlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.dto.response.LoginResponse;
import me.honki12345.hoonlog.dto.response.UserAccountResponse;
import me.honki12345.hoonlog.error.ErrorResponse;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "JWT API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final UserAccountService userAccountService;

    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호를 받아 로그인을 합니다",
        responses = {
            @ApiResponse(responseCode = "201", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401",
                description = "로그인에 실패하였습니다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @PostMapping("/token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UserAccountDTO userAccountDTO = userAccountService.findUserAccountAfterCheckingPassword(
            request.toDTO());
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        LoginResponse loginResponse = LoginResponse.of(tokenDTO.accessToken(),
            tokenDTO.refreshToken(), userAccountDTO.id(), userAccountDTO.username());
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @Operation(
        summary = "로그아웃",
        description = "로그인 상태의 유저의 토큰 데이터를 서버에서 삭제합니다",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400",
                description = "유효하지 않은 액세스 토큰 값일 경우 로그아웃에 실패합니다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @DeleteMapping("/token")
    public ResponseEntity<Object> logout(@RequestBody TokenDTO tokenDTO) {
        authService.deleteRefreshToken(tokenDTO.refreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
        summary = "액세스토큰 재발급",
        description = "리프레쉬 토큰이 유효하면 액세스 토큰 재발급을 해줍니다",
        responses = {
            @ApiResponse(responseCode = "201", description = "액세스토큰 재발급 성공"),
            @ApiResponse(responseCode = "404",
                description = "유효하지 않은 리프레쉬 토큰으로 요청시 재발급에 실패합니다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody TokenDTO tokenDTO) {
        Long userId = authService.FindUserIdByRefreshToken(tokenDTO.refreshToken());
        UserAccountDTO userAccountDTO = userAccountService.findUserAccountByUserId(userId);
        TokenDTO createdtokenDTO = authService.refreshAccessToken(tokenDTO.refreshToken());
        LoginResponse loginResponse = LoginResponse.of(createdtokenDTO.accessToken(),
            createdtokenDTO.refreshToken(), userAccountDTO.id(), userAccountDTO.username());
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }
}
