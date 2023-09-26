package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.response.UserAccountAddResponse;
import me.honki12345.hoonlog.dto.response.UserAccountDetailsResponse;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserAccountController {
    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountAddResponse> userAccountAdd(@Valid @RequestBody UserAccountAddRequest request) {
        UserAccountAddResponse resp = UserAccountAddResponse.from(userAccountService.saveUserAccount(request));
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserAccountDetailsResponse> userAccountDetails(@PathVariable String userId) {
        UserAccountDetailsResponse resp = UserAccountDetailsResponse.from(userAccountService.findUserAccount(userId));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
