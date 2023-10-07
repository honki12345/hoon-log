package me.honki12345.hoonlog.repository;

import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.repository.querydsl.UserAccountRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>,
    UserAccountRepositoryCustom {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
