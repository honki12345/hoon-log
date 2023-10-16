package me.honki12345.hoonlog.security.jwt.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class IfLoginAnnotationTestController {

    @GetMapping
    public ResponseEntity<Object> testForIfLoginAnnotation(@IfLogin String string) {
        return ResponseEntity.ok().build();
    }

}
