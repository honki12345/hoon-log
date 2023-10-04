package me.honki12345.hoonlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import me.honki12345.hoonlog.domain.vo.AuditingFields;

@Getter
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

    public PostImage() {
    }

    public void addPost(Post post) {
        this.post = post;
    }

    public void updatePostImage(String originalImgName, String imgName, String imgUrl) {
        this.originalImgName = originalImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
