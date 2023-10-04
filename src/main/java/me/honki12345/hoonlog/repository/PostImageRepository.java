package me.honki12345.hoonlog.repository;

import java.util.List;
import me.honki12345.hoonlog.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(Long postId);
}
