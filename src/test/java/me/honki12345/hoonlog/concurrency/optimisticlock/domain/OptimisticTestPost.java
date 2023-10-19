package me.honki12345.hoonlog.concurrency.optimisticlock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestPostLike;
import me.honki12345.hoonlog.concurrency.pessimisticlock.domain.PessimisticTestUserAccount;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OptimisticTestPost extends AuditingFields {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private OptimisticTestUserAccount userAccount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private long likeCount;

    @Version
    private Long version;

    @OneToMany(mappedBy = "post")
    private Set<OptimisticTestPostLike> postLikes = new ConcurrentSkipListSet<>();

    private OptimisticTestPost(Long id, OptimisticTestUserAccount userAccount, String title, String content) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static OptimisticTestPost of(Long id, String title, String content) {
        return OptimisticTestPost.of(id, null, title, content);
    }

    public static OptimisticTestPost of(Long id, OptimisticTestUserAccount userAccount, String title, String content) {
        return new OptimisticTestPost(id, userAccount, title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptimisticTestPost post)) {
            return false;
        }
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public OptimisticTestPost addUserAccount(OptimisticTestUserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public void addPostLike(OptimisticTestPostLike postLike) {
        this.postLikes.add(postLike);
        postLike.addPost(this);
        likeCount++;
    }

    public void deletePostLike(OptimisticTestPostLike postLike) {
        this.postLikes.remove(postLike);
        likeCount--;
    }
}
