package me.honki12345.hoonlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.domain.vo.Profile;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class UserAccount {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String userPassword;

    @Column(length = 100, unique = true)
    private String email;

    @Embedded
    private Profile profile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "userAccount")
    private Set<PostLike> postLikes = new LinkedHashSet<>();

    private UserAccount(Long id, String username, String userPassword, String email,
        Profile profile,
        LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.userPassword = userPassword;
        this.email = email;
        this.profile = profile;
        this.createdAt = createdAt;
    }

    public static UserAccount of(String username, String userPassword, String email,
        Profile profile) {
        return new UserAccount(null, username, userPassword, email, profile, null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccount that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addPostLike(PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.addUserAccount(this);
    }

    public void deletePostLike(PostLike postLike) {
        this.postLikes.remove(postLike);
    }
}
