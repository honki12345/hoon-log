package me.honki12345.hoonlog.controller;

import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public ResponseEntity<Object> testForMethodHandler(@IfLogin String string) {
        return ResponseEntity.ok().build();
    }

}
