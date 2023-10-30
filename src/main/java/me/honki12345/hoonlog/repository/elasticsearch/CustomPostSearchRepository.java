package me.honki12345.hoonlog.repository.elasticsearch;

import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostSearchRepository {

    Page<Post> searchByKeyword(String keyword, Pageable pageable);
}
