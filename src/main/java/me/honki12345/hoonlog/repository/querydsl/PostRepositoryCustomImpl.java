package me.honki12345.hoonlog.repository.querydsl;

import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.QPost;
import me.honki12345.hoonlog.domain.QPostComment;
import me.honki12345.hoonlog.domain.QPostImage;
import me.honki12345.hoonlog.domain.QTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class PostRepositoryCustomImpl extends QuerydslRepositorySupport implements PostRepositoryCustom {

    public PostRepositoryCustomImpl() {
        super(Post.class);
    }

    @Override
    public Page<Post> findByTagName(String tagName, Pageable pageable) {
        QPost post = QPost.post;
        QTag tag = QTag.tag;
        QPostComment postComment = QPostComment.postComment;
        QPostImage postImage = QPostImage.postImage;

        JPQLQuery<Post> query = from(post)
            .innerJoin(post.tags, tag).fetchJoin()
            .leftJoin(post.postComments, postComment).fetchJoin()
            .leftJoin(post.postImages, postImage).fetchJoin()
            .where(tag.name.in(tagName));

        List<Post> posts = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(posts, pageable, query.fetchCount());


    }
}
