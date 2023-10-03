package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.dto.response.UserAccountResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponse> addUserAccount(
        @Valid @RequestBody UserAccountAddRequest request) {
        UserAccountDTO dto = userAccountService.saveUserAccount(request.toDTO());
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserAccountResponse> searchUserAccount(@IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable String username) {
        if (userAccountPrincipal == null || !userAccountPrincipal.username().equals(username)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        UserAccountDTO dto = userAccountService.findUserAccountByUsername(username);
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserAccountResponse> modifyUserAccount(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable String username,
        @Valid @RequestBody UserAccountModifyRequest request) {
        if (userAccountPrincipal == null || !userAccountPrincipal.username().equals(username)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        UserAccountDTO dto = userAccountService.modifyUserAccount(username, request.toDTO());
        UserAccountResponse response = UserAccountResponse.from(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
