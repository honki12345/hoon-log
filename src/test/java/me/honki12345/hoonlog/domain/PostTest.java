package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("post 도메인 단위테스트")
class PostTest {


    @DisplayName("equals 메소드 테스트1")
    @Test
    void equals_test1() {
        // given
        Post post1 = Post.of(1L, null, null);
        String post2 = "hi";

        // when // then
        assertThat(post1.equals(post2)).isFalse();
    }

    @DisplayName("equals 메소드 테스트2")
    @Test
    void equals_test2() {
        // given
        Post post1 = Post.of(1L, null, null);
        Post post2 = Post.of(1L, null, null);

        // when // then
        assertThat(post1.equals(post2)).isTrue();
    }

    @DisplayName("postLike 필드생성 테스트")
    @Test
    void posts_create_test() {
        // given
        Post post = Post.of(1L, null, null);

        // when // then
        assertThat(post.getPostLikes()).isEmpty();
    }

    @DisplayName("updateTags 메소드 테스트")
    @Test
    void updateTags_test() {
        // given
        Post post = Post.of(null, null, null);
        Tag tag1 = Tag.of("1");
        Tag tag2 = Tag.of("2");
        post.addTags(Set.of(tag1));

        // when
        post.updateTags(Set.of(tag2));

        // then
        assertThat(post.getTags()).contains(tag2);
    }
}