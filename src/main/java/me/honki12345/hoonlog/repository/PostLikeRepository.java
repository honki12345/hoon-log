package me.honki12345.hoonlog.repository;

import java.util.Optional;
import me.honki12345.hoonlog.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPost_IdAndUserAccount_Id(Long postId, Long userId);
    Optional<PostLike> findByPost_IdAndUserAccount_Id(Long postId, Long userId);
}
