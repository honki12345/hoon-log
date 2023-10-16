package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostLike 도메인 단위테스트")
class PostLikeTest {

    @DisplayName("PostLike 의 id 값이 같다면, equals 호출시, true를 반환한다")
    @Test
    void givenPostLikeHasSameId_whenEquals_thenReturnsTrue() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        PostLike postLike2 = PostLike.of(1L);

        // when // then
        assertThat(postLike1.equals(postLike2)).isTrue();
    }

    @DisplayName("PostLike 변수가 가리키는 인스턴스가 같다면, equals 호출시, true를 반환한다")
    @Test
    void givenPostLikeHasSameRef_whenEquals_thenReturnsTrue() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        PostLike postLike2 = postLike1;


        // when // then
        assertThat(postLike1.equals(postLike2)).isTrue();
    }

    @DisplayName("다른 타입의 객체로, equals 호출시, false를 반환한다")
    @Test
    void givenAntherType_whenEquals_thenReturnsFalse() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        String postLike2 = "hello";

        // when // then
        assertThat(postLike1.equals(postLike2)).isFalse();
    }

    @DisplayName("UserAccount와 Post 객체로, PostLike 객체를 생성한다")
    @Test
    void givenUserAccountAndPost_whenCreatingPostLike_thenReturnsPostLikeHasUserAccountAndPost() {
        // given
        UserAccount userAccount = UserAccount.of(1L, null, null, null, null, null);
        Post post = Post.of(1L, null, null);
        PostLike postLike = PostLike.of(userAccount, post);

        // when // then
        assertThat(postLike.getUserAccount()).isEqualTo(userAccount);
        assertThat(postLike.getPost()).isEqualTo(post);
        assertThat(postLike.getId()).isNull();
    }
}