package me.honki12345.hoonlog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostLikeDTO;
import me.honki12345.hoonlog.service.PostLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/like")
@RestController
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<Object> create(
        @Valid @RequestBody PostLikeDTO postLikeDTO) {
        postLikeService.create(postLikeDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(
        @Valid @RequestBody PostLikeDTO postLikeDTO
    ) {
        postLikeService.delete(postLikeDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
