package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.dto.response.LoginResponse;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final UserAccountService userAccountService;

    @PostMapping("/token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UserAccountDTO userAccountDTO = userAccountService.findUserAccountAfterCheckingPassword(
            request);
        TokenDTO tokenDTO = authService.createTokens(userAccountDTO);
        LoginResponse loginResponse = LoginResponse.of(tokenDTO.accessToken(),
            tokenDTO.refreshToken(), userAccountDTO.id(), userAccountDTO.username());
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/token")
    public ResponseEntity<Object> logout(@RequestBody TokenDTO tokenDTO) {
        authService.deleteRefreshToken(tokenDTO.refreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

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
