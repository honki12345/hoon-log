package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.TagDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.TagService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> searchPost(@PathVariable Long postId) {
        return new ResponseEntity<>(PostResponse.from(postService.searchPost(postId)),
            HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> searchPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        Page<PostResponse> responses = postService.searchPosts(pageable)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/tag")
    public ResponseEntity<Page<PostResponse>> searchPostsByTag(
        @RequestParam("tagName") String tagName,
        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        TagDTO tagDTO = TagDTO.fromWithoutPostIds(tagService.searchTag(tagName));
        Page<PostResponse> responses = postService.searchPostsByTagName(pageable, tagDTO)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<PostResponse> addPost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @RequestParam(name = "postImageFiles", required = false) List<MultipartFile> postImageFiles,
        @Valid PostRequest postRequest) {

        PostResponse response = PostResponse.from(
            postService.addPost(postRequest.toDTO(),
                postImageFiles,
                postRequest.tagNames(),
                userAccountPrincipal.toDTO()));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable Long postId,
        @RequestParam(name = "postImageFiles", required = false) List<MultipartFile> postImageFiles,
        @Valid PostRequest postRequest) {
        PostDTO postDTO = PostDTO.from(
            postService.updatePost(postId, userAccountPrincipal.toDTO(),
                postRequest.toDTO(), postImageFiles, postRequest.tagNames()));
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
