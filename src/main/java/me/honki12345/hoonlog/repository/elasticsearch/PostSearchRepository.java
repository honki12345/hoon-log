package me.honki12345.hoonlog.repository.elasticsearch;

import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<Post, Long> {

    public Page<Post> findByTitleContainingOrContentContaining(String title, String content,
        Pageable pageable);

}
