package me.honki12345.hoonlog.repository.querydsl;

import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface PostRepositoryCustom {

    Page<Post> findByTagName(String tagName, Pageable pageable);

    Optional<Post> findByPostIdWithAll(@Param("postId") Long postId);
    Page<Post> findAllWithAll(Pageable pageable);

}
