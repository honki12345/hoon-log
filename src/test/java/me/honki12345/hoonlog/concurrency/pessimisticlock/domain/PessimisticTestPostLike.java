package me.honki12345.hoonlog.concurrency.pessimisticlock.domain;

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
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PessimisticTestPostLike extends AuditingFields {

    @Id
    @Column(name = "post_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private PessimisticTestUserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private PessimisticTestPost post;

    private PessimisticTestPostLike(Long id) {
        this.id = id;
    }

    private PessimisticTestPostLike(PessimisticTestUserAccount userAccount, PessimisticTestPost post) {
        this.userAccount = userAccount;
        this.post = post;
    }

    public static PessimisticTestPostLike of(Long id) {
        return new PessimisticTestPostLike(id);
    }

    public static PessimisticTestPostLike of(PessimisticTestUserAccount userAccount, PessimisticTestPost post) {
        return new PessimisticTestPostLike(userAccount, post);
    }

    public static PessimisticTestPostLike createEmptyPostLike() {
        return new PessimisticTestPostLike(null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessimisticTestPostLike postLike)) {
            return false;
        }
        return Objects.equals(id, postLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addPost(PessimisticTestPost post) {
        this.post = post;
    }

    public void addUserAccount(PessimisticTestUserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
