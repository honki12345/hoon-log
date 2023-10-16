package me.honki12345.hoonlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostCommentDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.dto.response.PostCommentResponse;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.ErrorResponse;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "comments", description = "댓글 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class PostCommentController {

    private final PostCommentService postCommentService;

    @Operation(
        summary = "댓글 생성",
        description = "로그인한 유저는 특정 게시글에 댓글을 생성할 수 있다",
        responses = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostCommentResponse.class)))),
            @ApiResponse(responseCode = "401", description = "댓글 제목을 입력하지 않아 생성에 실패한다", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
        }
    )
    @PostMapping
    public ResponseEntity<PostCommentResponse> addPostComment(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @Valid @RequestBody PostCommentRequest request) {
        PostCommentDTO postCommentDTO = postCommentService.addPostComment(request.toDTO(),
            request.postId(),
            userAccountPrincipal.toDTO());

        return new ResponseEntity<>(PostCommentResponse.from(postCommentDTO), HttpStatus.CREATED);
    }

    @Operation(
        summary = "댓글 수정",
        description = "해당 댓글을 작성한 유저는 댓글을 수정할 수 있다",
        parameters = {
            @Parameter(name = "commentId", description = "수정할려는 댓글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostCommentResponse.class)))),
            @ApiResponse(responseCode = "401", description = "댓글 제목을 입력하지 않아 생성에 실패한다", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
        }
    )
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

    @Operation(
        summary = "댓글 삭제",
        description = "해당 댓글을 작성한 유저는 댓글을 삭제게할 수 있다",
        parameters = {
            @Parameter(name = "commentId", description = "삭제할려는 댓글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostCommentResponse.class))))
        }
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deletePostComment(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable Long commentId
    ) {
        postCommentService.deleteComment(commentId, userAccountPrincipal.toDTO());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
