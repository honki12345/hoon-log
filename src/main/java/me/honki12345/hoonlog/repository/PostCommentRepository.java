package me.honki12345.hoonlog.repository;

import me.honki12345.hoonlog.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

}
