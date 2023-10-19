package me.honki12345.hoonlog.concurrency.pessimisticlock.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PessimisticTestPostRepository extends JpaRepository<PessimisticTestPost, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PessimisticTestPost p "
        + "LEFT JOIN FETCH p.postLikes "
        + "WHERE p.id = :postId")
    Optional<PessimisticTestPost> findByPostIdWithAll(@Param("postId") Long postId);
}
