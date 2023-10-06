package me.honki12345.hoonlog.repository;

import java.util.Optional;
import me.honki12345.hoonlog.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
