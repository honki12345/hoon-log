package me.honki12345.hoonlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Tag extends AuditingFields {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new LinkedHashSet<>();

    public Tag(Long id, String name, Set<Post> posts) {
        this.id = id;
        this.name = name;
        this.posts = posts;
    }

    private Tag(String name) {
        this.name = name;
    }

    public static Tag of(String tagName) {
        return new Tag(tagName);
    }

    public static Tag of(Long id, String name, Set<Post> posts) {
        return new Tag(id, name, posts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag tag)) {
            return false;
        }
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Tag addPost(Post post) {
        this.posts.add(post);
        return this;
    }
}
