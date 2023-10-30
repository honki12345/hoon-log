package me.honki12345.hoonlog.repository.elasticsearch;

import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<Post, Long>, CustomPostSearchRepository {

}
