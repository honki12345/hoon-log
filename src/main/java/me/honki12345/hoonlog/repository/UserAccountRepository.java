package me.honki12345.hoonlog.repository;

import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
