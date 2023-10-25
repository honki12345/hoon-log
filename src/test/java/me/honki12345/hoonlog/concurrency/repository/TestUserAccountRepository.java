package me.honki12345.hoonlog.concurrency.repository;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.domain.TestUserAccount;
import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestUserAccountRepository extends JpaRepository<TestUserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM TestUserAccount u "
        + "LEFT JOIN FETCH u.postLikes "
        + "WHERE u.id = :userId")
    Optional<TestUserAccount> findByIdWithPostLike(@Param("userId") Long userId);
}
