package me.honki12345.hoonlog.repository;

import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.repository.querydsl.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
