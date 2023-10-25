package me.honki12345.hoonlog.concurrency.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import me.honki12345.hoonlog.concurrency.domain.TestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestPostRepository extends JpaRepository<TestPost, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM TestPost p "
        + "LEFT JOIN FETCH p.postLikes "
        + "WHERE p.id = :postId")
    Optional<TestPost> findByPostIdWithAll(@Param("postId") Long postId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM TestPost p "
        + "LEFT JOIN FETCH p.postLikes "
        + "WHERE p.id = :postId")
    Optional<TestPost> findByPostIdWithAll2(@Param("postId") Long postId);
}
