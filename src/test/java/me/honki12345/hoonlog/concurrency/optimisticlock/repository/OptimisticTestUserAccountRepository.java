package me.honki12345.hoonlog.concurrency.optimisticlock.repository;

import java.util.Optional;
import me.honki12345.hoonlog.concurrency.optimisticlock.domain.OptimisticTestUserAccount;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptimisticTestUserAccountRepository extends JpaRepository<OptimisticTestUserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM OptimisticTestUserAccount u "
        + "LEFT JOIN FETCH u.postLikes "
        + "WHERE u.id = :userId")
    Optional<OptimisticTestUserAccount> findByIdWithPostLike(@Param("userId") Long userId);
}
