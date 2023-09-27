package me.honki12345.hoonlog.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.dto.ProfileDTO;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Profile {
    @Column(length = 100, nullable = false)
    private String blogName;

    @Column(columnDefinition = "TEXT")
    private String blogShortBio;

    private Profile(String blogName, String blogShortBio) {
        this.blogName = blogName;
        this.blogShortBio = blogShortBio;
    }

    public static Profile of(String blogName, String blogShortBio) {
        return new Profile(blogName, blogShortBio);
    }

    public void modify(ProfileDTO dto) {
        this.blogName = dto.blogName();
        this.blogShortBio = dto.blogShortBio();
    }
}
