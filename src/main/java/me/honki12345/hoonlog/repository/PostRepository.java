package me.honki12345.hoonlog.repository;

import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p "
        + "LEFT JOIN FETCH p.postImages LEFT JOIN FETCH p.postComments LEFT JOIN FETCH p.tags "
        + "WHERE p.id = :postId")
    Optional<Post> findByPostIdWithAll(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p "
        + "LEFT JOIN FETCH p.postImages LEFT JOIN FETCH p.postComments LEFT JOIN FETCH p.tags")
    Page<Post> findAllWithAll(Pageable pageable);
}
