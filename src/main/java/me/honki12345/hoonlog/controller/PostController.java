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
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.TagDTO;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.dto.security.UserAccountPrincipal;
import me.honki12345.hoonlog.error.ErrorResponse;
import me.honki12345.hoonlog.security.jwt.util.IfLogin;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@Tag(name = "posts", description = "게시글 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    public static final int PAGEABLE_DEFAULT_SIZE = 10;
    public static final String PAGEABLE_DEFAULT_SORT_COLUMN = "createdAt";
    private final PostService postService;
    private final TagService tagService;

    @Operation(
        summary = "게시글 상세 조회",
        description = "게시글 번호를 통해 게시글을 상세 조회 합니다",
        parameters = {
            @Parameter(name = "postId", description = "조회할려는 게시글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 번호로 조회하여 실패한다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> searchPost(@PathVariable Long postId) {
        return new ResponseEntity<>(PostResponse.from(postService.searchPost(postId)),
            HttpStatus.OK);
    }

    @Operation(
        summary = "게시글 리스트 조회",
        description = "주어진 페이지네이션, 검색키워드에 따라 게시글 리스트를 조회한다",
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)))),
        }
    )
    @GetMapping
    public ResponseEntity<Page<PostResponse>> searchPosts(
        @RequestParam(required = false) String keyword,

        @Parameter(description = "default 값: size=" + PAGEABLE_DEFAULT_SIZE + " sort=" + PAGEABLE_DEFAULT_SORT_COLUMN + " direction=DESC")
        @PageableDefault(size = PAGEABLE_DEFAULT_SIZE, sort = PAGEABLE_DEFAULT_SORT_COLUMN, direction = Direction.DESC) Pageable pageable) {
        Page<PostResponse> responses = postService.searchPosts(keyword, pageable)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
        summary = "태그를 통한 게시글 리스트 조회",
        description = "주어진 페이지네이션과 태그이름을 통해 게시글 리스트를 조회한다",
        parameters = {
            @Parameter(name = "postId", description = "조회할려는 게시글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)))),
        }
    )
    @GetMapping("/tag")
    public ResponseEntity<Page<PostResponse>> searchPostsByTag(
        @RequestParam("tagName") String tagName,

        @Parameter(description = "default 값: size=" + PAGEABLE_DEFAULT_SIZE + " sort=" + PAGEABLE_DEFAULT_SORT_COLUMN + " direction=DESC")
        @PageableDefault(size = PAGEABLE_DEFAULT_SIZE, sort = PAGEABLE_DEFAULT_SORT_COLUMN, direction = Direction.DESC) Pageable pageable) {
        TagDTO tagDTO = TagDTO.fromWithoutPostIds(tagService.searchTag(tagName));
        Page<PostResponse> responses = postService.searchPostsByTagName(pageable, tagDTO)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
        summary = "게시물 생성",
        description = "로그인한 유저는 게시글을 생성할 수 있다",
        responses = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)))),
            @ApiResponse(responseCode = "401", description = "게시글 제목을 입력하지 않아 생성에 실패한다", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
        }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> addPost(
        @Parameter(hidden = true)
        @IfLogin UserAccountPrincipal userAccountPrincipal,

        @Parameter(description = "이미지 첨부파일들: MultipartFile 클래스")
        @RequestParam(name = "postImageFiles", required = false) List<MultipartFile> postImageFiles,

        @Parameter(description = "게시물 생성 요청 데이터: Schema 참조")
        @Valid PostRequest postRequest) {
        PostResponse response = PostResponse.from(
            postService.addPost(postRequest.toDTO(),
                postImageFiles,
                postRequest.tagNames(),
                userAccountPrincipal.toDTO()));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "게시글 수정",
        description = "로그인한 유저가 작성자라면 게시글 번호를 통해 게시글을 수정합니다",
        parameters = {
            @Parameter(name = "postId", description = "수정할려는 게시글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "401", description = "게시글 제목을 입력하지 않아 생성에 실패한다", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 번호로 조회하여 실패한다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        }
    )
    @PutMapping(
        path = "/{postId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> updatePost(
        @Parameter(hidden = true)
        @IfLogin UserAccountPrincipal userAccountPrincipal,

        @Parameter(description = "수정 요청 게시글 번호")
        @PathVariable Long postId,

        @Parameter(description = "이미지 첨부파일들: MultipartFile 클래스")
        @RequestParam(name = "postImageFiles", required = false) List<MultipartFile> postImageFiles,

        @Parameter(description = "게시물 수정 요청 데이터: Schema 참조")
        @Valid PostRequest postRequest) {
        PostDTO postDTO = PostDTO.from(
            postService.updatePost(postId, userAccountPrincipal.toDTO(),
                postRequest.toDTO(), postImageFiles, postRequest.tagNames()));
        return new ResponseEntity<>(PostResponse.from(postDTO), HttpStatus.OK);
    }

    @Operation(
        summary = "게시글 삭제",
        description = "로그인한 유저가 작성자라면 게시글 번호를 통해 게시글을 삭제합니다",
        parameters = {
            @Parameter(name = "postId", description = "삭제할려는 게시글 번호", in = ParameterIn.PATH)},
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 번호로 조회하여 실패한다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        }
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(
        @IfLogin UserAccountPrincipal userAccountPrincipal,
        @PathVariable Long postId) {
        postService.deletePost(postId, userAccountPrincipal.toDTO());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
