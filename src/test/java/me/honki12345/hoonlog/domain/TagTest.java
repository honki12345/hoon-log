package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("tag 도메인 단위테스트")
class TagTest {

    @DisplayName("equals 메소드 테스트1")
    @Test
    void equals_test1() {
        // given
        Tag tag1 = Tag.of("hi");
        Tag tag2 = Tag.of("hi");

        // when // then
        assertThat(tag1.equals(tag2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트2")
    @Test
    void equals_test2() {
        // given
        Tag tag1 = Tag.of("hi");
        Tag tag2 = tag1;

        // when // then
        assertThat(tag1.equals(tag2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트3")
    @Test
    void equals_test3() {
        // given
        Tag tag1 = Tag.of("hi");
        String tag2 = "hello";

        // when // then
        assertThat(tag1.equals(tag2)).isFalse();
    }

    @DisplayName("deletePost 메소드 테스트")
    @Test
    void delete_post_test() {
        // given
        Post post = Post.of(1L, null, null);
        Tag tag = Tag.of("tag");

        // when // then
        assertDoesNotThrow(() -> tag.deletePost(post));
    }

    @DisplayName("posts 필드생성 테스트")
    @Test
    void posts_create_test() {
        // given
        Tag tag = Tag.of("tag");

        // when // then
        assertThat(tag.getPosts()).isEmpty();
    }

}