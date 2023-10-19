package me.honki12345.hoonlog.concurrency.pessimisticlock.repository;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessimisticTestPostLikeRepository extends JpaRepository<PessimisticTestPostLike, Long> {

    boolean existsByPost_IdAndUserAccount_Id(Long postId, Long userId);
    Optional<PessimisticTestPostLike> findByPost_IdAndUserAccount_Id(Long postId, Long userId);
}
