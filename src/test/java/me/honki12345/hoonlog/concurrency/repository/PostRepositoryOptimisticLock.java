package me.honki12345.hoonlog.concurrency.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepositoryOptimisticLock extends JpaRepository<Post, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Post p "
        + "LEFT JOIN FETCH p.postLikes "
        + "WHERE p.id = :postId")
    Optional<Post> findByPostOnOptimisticLock(@Param("postId") Long postId);
}
