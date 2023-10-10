package me.honki12345.hoonlog.domain;

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
import org.springframework.security.core.parameters.P;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostLike extends AuditingFields {

    @Id
    @Column(name = "post_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    private PostLike(Long id) {
        this.id = id;
    }

    private PostLike(UserAccount userAccount, Post post) {
        this.userAccount = userAccount;
        this.post = post;
    }

    public static PostLike of(Long id) {
        return new PostLike(id);
    }

    public static PostLike of(UserAccount userAccount, Post post) {
        return new PostLike(userAccount, post);
    }

    public static PostLike createEmptyPostLike() {
        return new PostLike(null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostLike postLike)) {
            return false;
        }
        return Objects.equals(id, postLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addPost(Post post) {
        this.post = post;
    }

    public void addUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
