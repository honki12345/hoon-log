package me.honki12345.hoonlog.concurrency.repository;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.domain.TestPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestPostLikeRepository extends JpaRepository<TestPostLike, Long> {

    boolean existsByPost_IdAndUserAccount_Id(Long postId, Long userId);
    Optional<TestPostLike> findByPost_IdAndUserAccount_Id(Long postId, Long userId);
}
