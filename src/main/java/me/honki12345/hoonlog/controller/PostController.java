package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        @RequestParam(name = "postImageFile", required = false) List<MultipartFile> postImageFileList,
        @Valid @RequestBody PostRequest postRequest) {

        PostDTO postDTO = postService.addPost(postRequest.toDTO(),
            postImageFileList,
            userAccountPrincipal.toDTO());
        return new ResponseEntity<>(PostResponse.from(postDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable Long postId,
        @Valid @RequestBody PostRequest postRequest) {
        PostDTO postDTO = postService.updatePost(postId, userAccountPrincipal.toDTO(),
            postRequest.toDTO());
        return new ResponseEntity<>(PostResponse.from(postDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable Long postId) {
        postService.deletePost(postId, userAccountPrincipal.toDTO());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
