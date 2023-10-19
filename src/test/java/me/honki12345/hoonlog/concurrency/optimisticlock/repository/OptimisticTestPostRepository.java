package me.honki12345.hoonlog.concurrency.optimisticlock.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPost;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptimisticTestPostRepository extends JpaRepository<OptimisticTestPost, Long> {

//    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM OptimisticTestPost p "
        + "LEFT JOIN FETCH p.postLikes "
        + "WHERE p.id = :postId")
    Optional<OptimisticTestPost> findByPostIdWithAll(@Param("postId") Long postId);
}
