package me.honki12345.hoonlog.repository.querydsl;

import static me.honki12345.hoonlog.domain.QPostLike.*;
import static me.honki12345.hoonlog.domain.QUserAccount.*;

import java.util.Optional;
import me.honki12345.hoonlog.domain.QPostLike;
import me.honki12345.hoonlog.domain.QUserAccount;
import me.honki12345.hoonlog.domain.UserAccount;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class UserAccountRepositoryCustomImpl extends QuerydslRepositorySupport implements
    UserAccountRepositoryCustom {

    public UserAccountRepositoryCustomImpl() {
        super(UserAccount.class);
    }

    @Override
    public Optional<UserAccount> findByIdFetchJoin(Long userId) {
        UserAccount fetchedOne = from(userAccount)
            .leftJoin(userAccount.postLikes, postLike).fetchJoin()
            .where(userAccount.id.eq(userId))
            .fetchOne();
        return Optional.ofNullable(fetchedOne);
    }

    @Override
    public Optional<UserAccount> findByUsernameFetchJoin(String username) {
        UserAccount fetchedOne = from(userAccount)
            .leftJoin(userAccount.postLikes, postLike).fetchJoin()
            .where(userAccount.username.eq(username))
            .fetchOne();
        return Optional.ofNullable(fetchedOne);
    }
}
