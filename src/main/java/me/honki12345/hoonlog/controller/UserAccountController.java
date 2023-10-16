package me.honki12345.hoonlog.controller;

import static me.honki12345.hoonlog.error.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.dto.response.UserAccountResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.ErrorResponse;
import me.honki12345.hoonlog.error.exception.domain.ModifyUserAccountForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.SearchUserAccountForbiddenException;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "users", description = "회원 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    @Operation(
        summary = "회원가입",
        description = "아이디, 비밀번호, 이메일과, 개인 블로그 이름과 소개글을 입력받아 회원가입 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserAccountResponse.class))),
            @ApiResponse(responseCode = "400", description = """
                1. 아이디를 입력하지 않으면 실패한다
                2. 중복된 아이디를 입력하면 실패한다
                3. 개인 블로그 제목을 입력하지 않으면 실패한다""", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @PostMapping
    public ResponseEntity<UserAccountResponse> addUserAccount(
        @Valid @RequestBody UserAccountAddRequest request) {
        UserAccountDTO dto = userAccountService.saveUserAccount(request.toDTO());
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, CREATED);
    }

    @Operation(
        summary = "회원정보 조회",
        description = "로그인한 회원은 자신의 회원정보를 조회할 수 있습니다",
        parameters = {@Parameter(name = "username", description = "조회할려는 회원 아이디", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공", content = @Content(schema = @Schema(implementation = UserAccountResponse.class))),
            @ApiResponse(responseCode = "403",
                description = "로그인한 유저와 조회 유저가 다른경우, 조회에 실패한다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @GetMapping("/{username}")
    public ResponseEntity<UserAccountResponse> searchUserAccount(
        @Parameter(name = "userAccountPrincipal", description = "로그인한 유저의 인증객체: SecurityContext 로부터 불러온다")
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable String username) {
        if (!userAccountPrincipal.username().equals(username)) {
            throw new SearchUserAccountForbiddenException(SEARCH_USER_ACCOUNT_FORBIDDEN);
        }
        UserAccountDTO dto = userAccountService.findUserAccountByUsername(username);
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, OK);
    }

    @Operation(
        summary = "회원정보 수정",
        description = "로그인한 회원은 자신의 회원정보를 수정할 수 있습니다",
        parameters = {@Parameter(name = "username", description = "수정할려는 회원 아이디", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정 성공", content = @Content(schema = @Schema(implementation = UserAccountResponse.class))),
            @ApiResponse(responseCode = "403",
                description = "로그인한 유저와 수정 유저가 다른경우, 수정에 실패한다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                description = "수정시 개인 블로그 제목을 미입력시 실패한다",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @PutMapping("/{username}")
    public ResponseEntity<UserAccountResponse> modifyUserAccount(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable String username,
        @Valid @RequestBody UserAccountModifyRequest request) {
        if (!userAccountPrincipal.username().equals(username)) {
            throw new ModifyUserAccountForbiddenException(MODIFY_USER_ACCOUNT_FORBIDDEN);
        }
        UserAccountDTO dto = userAccountService.modifyUserAccount(username, request.toDTO());
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, OK);
    }
}
