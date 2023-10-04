package me.honki12345.hoonlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.dto.response.PostCommentResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<PostCommentResponse> addPostComment(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @Valid @RequestBody PostCommentRequest request) {
        PostCommentDTO postCommentDTO = postCommentService.addPostComment(request.toDTO(), request.postId(),
            userAccountPrincipal.toDTO());

        return new ResponseEntity<>(PostCommentResponse.from(postCommentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<PostCommentResponse> modifyPostComment(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @Valid @RequestBody PostCommentRequest request,
        @PathVariable Long commentId
    ) {
        PostCommentDTO postCommentDTO = postCommentService.modifyComment(request.toDTO(), commentId,
            userAccountPrincipal.toDTO());
        return new ResponseEntity<>(PostCommentResponse.from(postCommentDTO), HttpStatus.OK);
    }

}
