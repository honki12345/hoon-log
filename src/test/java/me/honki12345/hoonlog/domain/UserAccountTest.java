package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserAccount 도메인 단위테스트")
class UserAccountTest {

    @DisplayName("postlike constructor 테스트")
    @Test
    void postLikes_constructor_test() {
        // given
        UserAccount userAccount = UserAccount.of(null, null, null, null);

        // when
        Set<PostLike> postLikes = userAccount.getPostLikes();

        // then
        assertThat(postLikes).isNotNull();
    }

    @DisplayName("equals 메소드 테스트1")
    @Test
    void equals_test1() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = UserAccount.of(1L, null, null, null, null);

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트2")
    @Test
    void equals_test2() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = userAccount1;

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isTrue();
    }

    @DisplayName("equals 메소드 테스트3")
    @Test
    void equals_test3() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        String userAccount2 = "hello";

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isFalse();
    }

    @DisplayName("hashcode 메소드 테스트")
    @Test
    void hashcode_test() {
        // given
        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = UserAccount.of(1L, null, null, null, null);

        // when
        int i = userAccount1.hashCode();
        int i1 = userAccount2.hashCode();

        // then
        assertThat(i == i1).isTrue();
    }

    @DisplayName("생성자 메소드 of 테스트")
    @Test
    void constructor_test() {
        // given
        UserAccount userAccount = UserAccount.of(1L, null, null, null, null, null);

        // when // then
        assertThat(userAccount.getRoles()).isEmpty();
    }

    @DisplayName("생성자 메소드 of 테스트2")
    @Test
    void constructor_test2() {
        // given
        UserAccount userAccount = UserAccount.of(1L, null, null, null, null, Set.of());

        // when // then
        assertThat(userAccount.getRoles()).isEmpty();
    }
}