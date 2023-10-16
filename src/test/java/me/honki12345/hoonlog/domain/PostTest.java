package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("post 도메인 단위테스트")
class PostTest {


    @DisplayName("타입이 다른 객체로, equals 메소드를 실행하면, false를 리턴한다")
    @Test
    void givenAnotherTypeInstance_whenEquals_thenReturnsFalse() {
        // given
        Post post1 = Post.of(1L, null, null);
        String post2 = "hi";

        // when // then
        assertThat(post1.equals(post2)).isFalse();
    }

    @DisplayName("게시글 ID 값이 같은 객체로, equals 메소드를 실행하면, true를 리턴한다")
    @Test
    void givenAnotherPostWithSameId_whenEquals_thenReturnsTrue() {
        // given
        Post post1 = Post.of(1L, null, null);
        Post post2 = Post.of(1L, null, null);

        // when // then
        assertThat(post1.equals(post2)).isTrue();
    }

    @DisplayName("Post 객체를 만들면, postLikes 속성으로 빈 리스트를 가진다")
    @Test
    void givenPost_whenCreatingEmptyPost_thenReturnsPostHasEmptyPostLikes() {
        // given
        Post post = Post.of(1L, null, null);

        // when // then
        assertThat(post.getPostLikes()).isEmpty();
    }

    @DisplayName("updateTags()를 호출하면, post 속성의 tags의 값이 바뀐다")
    @Test
    void givenPost_whenUpdatingTags_thenPostHavesUpdatedTags() {
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