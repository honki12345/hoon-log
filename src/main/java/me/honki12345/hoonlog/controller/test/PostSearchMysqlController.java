package me.honki12345.hoonlog.controller.test;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.dto.response.PostResponse;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.test.PostSearchMysqlService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("search-performance-test")
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostSearchMysqlController {

    public static final int PAGEABLE_DEFAULT_SIZE = 10;
    public static final String PAGEABLE_DEFAULT_SORT_COLUMN = "createdAt";

    private final PostSearchMysqlService postService;


    @GetMapping("/mysql")
    public ResponseEntity<Page<PostResponse>> searchPostsByMysql(
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = PAGEABLE_DEFAULT_SIZE, sort = PAGEABLE_DEFAULT_SORT_COLUMN, direction = Direction.DESC) Pageable pageable) {
        Page<PostResponse> responses = postService.searchPostsByMysqlLike(keyword, pageable)
            .map(PostResponse::from);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
