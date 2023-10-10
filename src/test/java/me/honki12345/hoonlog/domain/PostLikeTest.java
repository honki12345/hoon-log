package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostLike 도메인 단위테스트")
class PostLikeTest {

    @DisplayName("equals 메소드 테스트1")
    @Test
    void equals_test1() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        PostLike postLike2 = PostLike.of(1L);

        // when // then
        assertThat(postLike1.equals(postLike2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트2")
    @Test
    void equals_test2() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        PostLike postLike2 = postLike1;


        // when // then
        assertThat(postLike1.equals(postLike2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트3")
    @Test
    void equals_test3() {
        // given
        PostLike postLike1 = PostLike.of(1L);
        String postLike2 = "hello";

        // when // then
        assertThat(postLike1.equals(postLike2)).isFalse();
    }

    @DisplayName("생성자 메소드 of 테스트")
    @Test
    void constructor_test() {
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