package me.honki12345.hoonlog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.domain.vo.AuditingFields;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "posts")
@Entity
public class Post extends AuditingFields {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private long likeCount;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostComment> postComments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostImage> postImages = new LinkedList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
        name = "post_tag",
        joinColumns = @JoinColumn(name = "postId"),
        inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private Set<PostLike> postLikes = new ConcurrentSkipListSet<>();

    @Version
    private Long version;

    private Post(Long id, UserAccount userAccount, String title, String content) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    @PersistenceCreator
    public Post(Long id, UserAccount userAccount, String title, String content, long likeCount,
        Set<PostComment> postComments, List<PostImage> postImages, Set<Tag> tags,
        Set<PostLike> postLikes, Long version) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.postComments = postComments;
        this.postImages = postImages;
        this.tags = tags;
        this.postLikes = postLikes;
        this.version = version;
    }

    public static Post of(Long id, String title, String content) {
        return Post.of(id, null, title, content);
    }

    public static Post of(Long id, UserAccount userAccount, String title, String content) {
        return new Post(id, userAccount, title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post post)) {
            return false;
        }
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Post addUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public Post addPostImage(PostImage postImage) {
        this.postImages.add(postImage);
        postImage.addPost(this);
        return this;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post addTags(Set<Tag> tags) {
        this.tags = tags;
        tags.forEach(tag -> tag.addPost(this));
        return this;
    }

    public Post updateTags(Set<Tag> newTags) {
        this.tags.forEach(tag -> tag.deletePost(this));
        this.tags = newTags;
        newTags.forEach(tag -> tag.addPost(this));
        return this;
    }

    public void addPostLike(PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.addPost(this);
        likeCount++;
    }

    public void deletePostLike(PostLike postLike) {
        this.postLikes.remove(postLike);
        likeCount--;
    }

    public void updateTimeAndWriter(String username, LocalDateTime time) {
        this.createdAt = time;
        this.modifiedAt = time;
        this.createdBy = username;
        this.modifiedBy = username;
    }
}
