package me.honki12345.hoonlog.concurrency.optimisticlock.repository;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimisticTestPostLikeRepository extends JpaRepository<OptimisticTestPostLike, Long> {

    boolean existsByPost_IdAndUserAccount_Id(Long postId, Long userId);
    Optional<OptimisticTestPostLike> findByPost_IdAndUserAccount_Id(Long postId, Long userId);
}
