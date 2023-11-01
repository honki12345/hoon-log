package me.honki12345.hoonlog.service.test;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.repository.PostRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Profile("search-performance-test")
@RequiredArgsConstructor
@Transactional
@Service
public class PostSearchMysqlService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<Post> searchPostsByMysqlLike(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return postRepository.findAllFetchJoin(pageable);
        }

        return postRepository.findFetchJoinByTitleContainingOrContentContaining(keyword,
            pageable);
    }


}
