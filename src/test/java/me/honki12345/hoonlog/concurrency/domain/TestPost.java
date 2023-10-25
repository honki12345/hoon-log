package me.honki12345.hoonlog.concurrency.domain;

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
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TestPost extends AuditingFields {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private TestUserAccount userAccount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private long likeCount;

    @Version
    private Long version;

    @OneToMany(mappedBy = "post")
    private Set<TestPostLike> postLikes = new ConcurrentSkipListSet<>();

    private TestPost(Long id, TestUserAccount userAccount, String title, String content) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static TestPost of(Long id, String title, String content) {
        return TestPost.of(id, null, title, content);
    }

    public static TestPost of(Long id, TestUserAccount userAccount, String title, String content) {
        return new TestPost(id, userAccount, title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestPost post)) {
            return false;
        }
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TestPost addUserAccount(TestUserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public void addPostLike(TestPostLike postLike) {
        this.postLikes.add(postLike);
        postLike.addPost(this);
        likeCount++;
    }

    public void deletePostLike(TestPostLike postLike) {
        this.postLikes.remove(postLike);
        likeCount--;
    }
}
