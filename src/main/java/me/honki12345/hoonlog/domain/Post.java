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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private final Set<PostComment> postComments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private List<PostImage> postImages = new LinkedList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tag",
        joinColumns = @JoinColumn(name = "postId"),
        inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostLike> postLikes = new ConcurrentSkipListSet<>();

    private Post(Long id, UserAccount userAccount, String title, String content) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    private Post(Long id, UserAccount userAccount, String title, String content,
        List<PostImage> postImages, Set<Tag> tags) {
        this.id = id;
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.postImages = postImages;
        this.tags = tags;
    }

    public static Post of(Long id, String title, String content) {
        return Post.of(id, null, title, content);
    }

    public static Post of(Long id, UserAccount userAccount, String title, String content) {
        return new Post(id, userAccount, title, content);
    }

    public static Post of(Long id, UserAccount userAccount, String title, String content,
        Set<Tag> tags) {
        return new Post(id, userAccount, title, content, null, tags);
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

    public Post addPostImages(Collection<PostImage> postImages) {
        postImages.forEach(postImage -> {
            this.postImages.add(postImage);
            postImage.addPost(this);
        });
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
        likeCount = this.postLikes.size();
    }

    public void deletePostLike(PostLike postLike) {
        this.postLikes.remove(postLike);
        likeCount = this.postLikes.size();
    }
}
