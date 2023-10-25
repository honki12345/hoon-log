package me.honki12345.hoonlog.concurrency.domain;

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
public class TestPostLike extends AuditingFields {

    @Id
    @Column(name = "post_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private TestUserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private TestPost post;

    private TestPostLike(Long id) {
        this.id = id;
    }

    private TestPostLike(TestUserAccount userAccount, TestPost post) {
        this.userAccount = userAccount;
        this.post = post;
    }

    public static TestPostLike of(Long id) {
        return new TestPostLike(id);
    }

    public static TestPostLike of(TestUserAccount userAccount, TestPost post) {
        return new TestPostLike(userAccount, post);
    }

    public static TestPostLike createEmptyPostLike() {
        return new TestPostLike(null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestPostLike postLike)) {
            return false;
        }
        return Objects.equals(id, postLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addPost(TestPost post) {
        this.post = post;
    }

    public void addUserAccount(TestUserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
