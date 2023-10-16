package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostComment 도메인 단위테스트")
class PostCommentTest {

    @DisplayName("PostComment 객체의 id 속성 값이 같다면, equals 메소드 호출시, true를 반환한다")
    @Test
    void givenPostCommentsHaveSameId_whenEquals_thenReturnsTrue() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        PostComment postComment2 = PostComment.of(1L, "content1");

        // when // then
        assertThat(postComment1.equals(postComment2)).isTrue();
    }

    @DisplayName("PostComment 변수가 같은 인스턴스를 가리킨다면, equals 메소드 호출시, true를 반환한다")
    @Test
    void givenPostCommentsHaveSameRef_whenEquals_thenReturnsTrue() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        PostComment postComment2 = postComment1;

        // when // then
        assertThat(postComment1.equals(postComment2)).isTrue();
    }

    @DisplayName("PostComment와다른 타입의 객체로, equals 메소드 호출시, false를 반환한다")
    @Test
    void givenAnotherType_whenEquals_thenReturnFalse() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");
        String postComment2 = "hi";

        // when // then
        assertThat(postComment1.equals(postComment2)).isFalse();
    }

    @DisplayName("null을 argument로, update를 호출시, 예외를 던진다")
    @Test
    void givenNull_whenUpdate_thenThrowsException() {
        // given
        PostComment postComment1 = PostComment.of(1L, "content2");

        // when // then
        assertThatNoException().isThrownBy(() -> postComment1.update(null));
    }
}