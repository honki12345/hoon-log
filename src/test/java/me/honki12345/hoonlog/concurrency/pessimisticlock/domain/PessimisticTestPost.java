package me.honki12345.hoonlog.concurrency.pessimisticlock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PessimisticTestPost extends AuditingFields {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private PessimisticTestUserAccount userAccount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private long likeCount;

    @OneToMany(mappedBy = "post")
    private Set<PessimisticTestPostLike> postLikes = new ConcurrentSkipListSet<>();

    private PessimisticTestPost(Long id, PessimisticTestUserAccount userAccount, String title, String content) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static PessimisticTestPost of(Long id, String title, String content) {
        return PessimisticTestPost.of(id, null, title, content);
    }

    public static PessimisticTestPost of(Long id, PessimisticTestUserAccount userAccount, String title, String content) {
        return new PessimisticTestPost(id, userAccount, title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessimisticTestPost post)) {
            return false;
        }
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public PessimisticTestPost addUserAccount(PessimisticTestUserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public void addPostLike(PessimisticTestPostLike postLike) {
        this.postLikes.add(postLike);
        postLike.addPost(this);
        likeCount++;
    }

    public void deletePostLike(PessimisticTestPostLike postLike) {
        this.postLikes.remove(postLike);
        likeCount--;
    }
}
