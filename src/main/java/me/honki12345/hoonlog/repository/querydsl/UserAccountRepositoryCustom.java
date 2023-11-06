package me.honki12345.hoonlog.repository.querydsl;

import java.util.Optional;
import me.honki12345.hoonlog.domain.UserAccount;

public interface UserAccountRepositoryCustom {

    Optional<UserAccount> findByIdFetchJoin(Long userId);
    Optional<UserAccount> findByUsernameFetchJoin(String username);
}
