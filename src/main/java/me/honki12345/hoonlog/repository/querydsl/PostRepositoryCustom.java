package me.honki12345.hoonlog.repository.querydsl;

import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<Post> findByTagName(String tagName, Pageable pageable);

}
