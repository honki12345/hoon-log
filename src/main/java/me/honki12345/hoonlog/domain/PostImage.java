package me.honki12345.hoonlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
public class PostImage extends AuditingFields {

    @Id
    @Column(name = "post_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName;

    private String originalImgName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private PostImage(Long id, String imgName, String originalImgName, String imgUrl, Post post) {
        this.id = id;
        this.imgName = imgName;
        this.originalImgName = originalImgName;
        this.imgUrl = imgUrl;
        this.post = post;
    }

    public static PostImage of(Post post) {
        PostImage postImage = new PostImage();
        postImage.addPost(post);
        return postImage;
    }

    public static PostImage of(String originalImgName, String imgName, String imgUrl) {
        return new PostImage(null, imgName, originalImgName, imgUrl, null);
    }

    public static PostImage of(Long id, String imgName, String originalImgName, String imgUrl,
        Post post) {
        return new PostImage(id, imgName, originalImgName, imgUrl, post);
    }

    public void addPost(Post post) {
        this.post = post;
    }

    public void updatePostImage(String originalFilename, String imgName, String imgUrl) {
        this.originalImgName = originalFilename;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
