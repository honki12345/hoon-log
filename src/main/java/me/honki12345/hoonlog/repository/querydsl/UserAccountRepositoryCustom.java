package me.honki12345.hoonlog.repository.querydsl;

import java.util.Optional;
import me.honki12345.hoonlog.domain.UserAccount;

public interface UserAccountRepositoryCustom {

    Optional<UserAccount> findByIdWithPostLike(Long userId);
}
