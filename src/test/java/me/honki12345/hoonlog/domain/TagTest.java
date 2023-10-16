package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("tag 도메인 단위테스트")
class TagTest {

    @DisplayName("id 값이 같은 태그 객체들로, equals 호출시, true를 반환한다")
    @Test
    void givenTagHasSameId_whenEquals_thenReturnsTrue() {
        // given
        Tag tag1 = Tag.of("hi");
        Tag tag2 = Tag.of("hi");

        // when // then
        assertThat(tag1.equals(tag2)).isTrue();
    }

    @DisplayName("태그 변수가 같은 인스턴스를 가리키면, equals 호출시, true를 호출한다")
    @Test
    void givenTagsIsSameRef_whenEquals_thenReturnsTrue() {
        // given
        Tag tag1 = Tag.of("hi");
        Tag tag2 = tag1;

        // when // then
        assertThat(tag1.equals(tag2)).isTrue();
    }

    @DisplayName("다른 타입의 객체로, equals 호출시, false를 반환한다")
    @Test
    void givenAnotherType_whenEquals_thenReturnsFalse() {
        // given
        Tag tag1 = Tag.of("hi");
        String tag2 = "hello";

        // when // then
        assertThat(tag1.equals(tag2)).isFalse();
    }

    @DisplayName("Post를 가지고 있지 않은 Tag로, deletePost(post) 호출시, 예외를 던진다")
    @Test
    void givenTagHasNotPost_whenDeletingPost_thenThrowsException() {
        // given
        Post post = Post.of(1L, null, null);
        Tag tag = Tag.of("tag");

        // when // then
        assertDoesNotThrow(() -> tag.deletePost(post));
    }

    @DisplayName("태그를 생성하면, 태그는, posts로 빈 리스트를 가진다")
    @Test
    void givenNothing_whenCreatingTag_returnsTagHasNotPost() {
        // given
        Tag tag = Tag.of("tag");

        // when // then
        assertThat(tag.getPosts()).isEmpty();
    }

}