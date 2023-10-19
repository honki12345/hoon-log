package me.honki12345.hoonlog.concurrency.optimisticlock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPost;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OptimisticTestPostLike extends AuditingFields {

    @Id
    @Column(name = "post_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private OptimisticTestUserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private OptimisticTestPost post;

    private OptimisticTestPostLike(Long id) {
        this.id = id;
    }

    private OptimisticTestPostLike(OptimisticTestUserAccount userAccount, OptimisticTestPost post) {
        this.userAccount = userAccount;
        this.post = post;
    }

    public static OptimisticTestPostLike of(Long id) {
        return new OptimisticTestPostLike(id);
    }

    public static OptimisticTestPostLike of(OptimisticTestUserAccount userAccount, OptimisticTestPost post) {
        return new OptimisticTestPostLike(userAccount, post);
    }

    public static OptimisticTestPostLike createEmptyPostLike() {
        return new OptimisticTestPostLike(null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptimisticTestPostLike postLike)) {
            return false;
        }
        return Objects.equals(id, postLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addPost(OptimisticTestPost post) {
        this.post = post;
    }

    public void addUserAccount(OptimisticTestUserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
