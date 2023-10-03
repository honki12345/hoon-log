package me.honki12345.hoonlog.repository;

import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
