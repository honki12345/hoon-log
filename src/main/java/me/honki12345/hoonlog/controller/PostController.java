package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> searchPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        Page<PostResponse> responses = postService.searchPosts(pageable)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> searchPost(@PathVariable Long postId) {
        PostResponse response = PostResponse.from(postService.searchPost(postId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostResponse> addPost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @Valid @RequestBody PostRequest postRequest) {

        PostDTO postDTO = postService.addPost(postRequest, userAccountPrincipal);
        PostResponse response = PostResponse.from(postDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
