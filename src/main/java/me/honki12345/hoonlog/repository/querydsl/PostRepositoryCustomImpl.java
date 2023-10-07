package me.honki12345.hoonlog.repository.querydsl;

import static me.honki12345.hoonlog.domain.QPost.*;
import static me.honki12345.hoonlog.domain.QPostComment.*;
import static me.honki12345.hoonlog.domain.QPostImage.*;
import static me.honki12345.hoonlog.domain.QPostLike.*;
import static me.honki12345.hoonlog.domain.QTag.*;

import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import java.util.Optional;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.QPostLike;
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

        JPQLQuery<Post> query = from(post)
            .innerJoin(post.tags, tag).fetchJoin()
            .leftJoin(post.postComments, postComment).fetchJoin()
            .leftJoin(post.postImages, postImage).fetchJoin()
            .where(tag.name.in(tagName));

        List<Post> posts = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(posts, pageable, query.fetchCount());


    }

    @Override
    public Optional<Post> findByPostIdWithAll(Long postId) {
        Post fetchedOne = from(post)
            .leftJoin(post.postImages, postImage).fetchJoin()
            .leftJoin(post.postComments, postComment).fetchJoin()
            .leftJoin(post.tags, tag).fetchJoin()
            .leftJoin(post.postLikes, postLike).fetchJoin()
            .where(post.id.eq(postId))
            .fetchOne();
        return Optional.ofNullable(fetchedOne);
    }

    @Override
    public Page<Post> findAllWithAll(Pageable pageable) {
        JPQLQuery<Post> query = from(post)
            .leftJoin(post.postImages, postImage).fetchJoin()
            .leftJoin(post.postComments, postComment).fetchJoin()
            .leftJoin(post.tags, tag).fetchJoin()
            .leftJoin(post.postLikes, postLike).fetchJoin();
        List<Post> posts = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(posts, pageable, query.fetchCount());
    }
}
