package me.honki12345.hoonlog.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "like", description = "게시물좋아요 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/like")
@RestController
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(
        summary = "게시글 좋아요 생성",
        description = "로그인한 유저는 게시글에 좋아요를 할 수 있습니다",
        responses = {
            @ApiResponse(responseCode = "201", description = "게시글 좋아요 성공")
        }
    )
    @PostMapping
    public ResponseEntity<Object> create(
        @Valid @RequestBody PostLikeDTO postLikeDTO) {
        postLikeService.create(postLikeDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
        summary = "게시글 좋아요 취소",
        description = "로그인한 유저는 좋아요를 했던 게시글에 취소를 할 수 있습니다",
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 취소 성공")
        }
    )
    @DeleteMapping
    public ResponseEntity<Object> delete(
        @Valid @RequestBody PostLikeDTO postLikeDTO
    ) {
        postLikeService.delete(postLikeDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
