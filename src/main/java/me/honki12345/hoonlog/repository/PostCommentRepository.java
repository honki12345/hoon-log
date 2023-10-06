package me.honki12345.hoonlog.repository;

import java.util.List;
import me.honki12345.hoonlog.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findAllByPost_id(Long postId);
}
