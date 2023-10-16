package me.honki12345.hoonlog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserAccount 도메인 단위테스트")
class UserAccountTest {

    @DisplayName("UserAccount 생성시, postLikes 속성으로, 빈 리스트를 가진다")
    @Test
    void givenNothing_whenCreatingUserAccount_thenUserAccountHasPostLikeEmptyList() {
        // given
        UserAccount userAccount = UserAccount.of(null, null, null, null);

        // when
        Set<PostLike> postLikes = userAccount.getPostLikes();

        // then
        assertThat(postLikes).isNotNull();
    }

    @DisplayName("UserAccount 객체의 id 가 같다면, eqauls 호출시, true를 반환한다")
    @Test
    void givenUserAccountsHaveSameId_whenEquals_thenReturnsTrue() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = UserAccount.of(1L, null, null, null, null);

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isTrue();
    }

    @DisplayName("UserAccount 변수가 같은 인스턴스를 가리킨다면, equals 호출시, true를 호출한다")
    @Test
    void givenUserAccountIsSameRef_whenEquals_thenReturnsTrue() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = userAccount1;

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isTrue();
    }

    @DisplayName("다른 타입의 객체와, equals 호출시, false를 반환한다")
    @Test
    void givenAnotherType_whenEquals_thenReturnsFalse() {
        // given

        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        String userAccount2 = "hello";

        // when // then
        assertThat(userAccount1.equals(userAccount2)).isFalse();
    }

    @DisplayName("UserAccount의 id가 같으면, hascode 비교하면, true를 반환한다")
    @Test
    void givenUserAccountsHasSameId_whenComparingHashCode_thenReturnsTrue() {
        // given
        UserAccount userAccount1 = UserAccount.of(1L, null, null, null, null);
        UserAccount userAccount2 = UserAccount.of(1L, null, null, null, null);

        // when
        int i = userAccount1.hashCode();
        int i1 = userAccount2.hashCode();

        // then
        assertThat(i == i1).isTrue();
    }

    @DisplayName("UserAccount를 생성하면, roles 속성으로 빈 컬렉션을 가진다")
    @Test
    void givenNothing_whenCreatingUserAccount_thenUserAccountHaveEmptyRoles() {
        // given
        UserAccount userAccount = UserAccount.of(1L, null, null, null, null, null);

        // when // then
        assertThat(userAccount.getRoles()).isEmpty();
    }

    @DisplayName("empty Set을 인자로, UserAccount를 생성하면, empty set을 가진다")
    @Test
    void givenNothing_whenCreatingUserAccountWithEmptySet_thenUserAccountHaveEmptyCollection() {
        // given
        UserAccount userAccount = UserAccount.of(1L, null, null, null, null, Set.of());

        // when // then
        assertThat(userAccount.getRoles()).isEmpty();
    }
}