package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostComment 도메인 단위테스트")
class PostCommentTest {

    @DisplayName("equals 메소드 테스트")
    @Test
    void equals_test() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        PostComment postComment2 = PostComment.of(1L, "content1");

        // when // then
        assertThat(postComment1.equals(postComment2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트2")
    @Test
    void equals_test2() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        PostComment postComment2 = postComment1;

        // when // then
        assertThat(postComment1.equals(postComment2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트3")
    @Test
    void equals_test3() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        String postComment2 = "hi";

        // when // then
        assertThat(postComment1.equals(postComment2)).isFalse();
    }

    @DisplayName("update 메소드 테스트1")
    @Test
    void update_test1() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");

        // when // then
        assertThatNoException().isThrownBy(() -> postComment1.update(null));
    }
}