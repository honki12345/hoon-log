package me.honki12345.hoonlog.repository.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomPostSearchRepositoryImpl implements CustomPostSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<Post> searchByKeyword(String keyword, Pageable pageable) {
        Criteria criteria = Criteria.where("title").contains(keyword)
            .or("content").contains(keyword);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<Post> searchHits = elasticsearchOperations.search(query, Post.class);
        SearchPage<Post> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        Page<Post> posts = (Page) SearchHitSupport.unwrapSearchHits(searchPage);
        return posts;
    }
}
