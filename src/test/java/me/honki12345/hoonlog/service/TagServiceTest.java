package me.honki12345.hoonlog.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import me.honki12345.hoonlog.error.exception.domain.TagNotFoundException;
import me.honki12345.hoonlog.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TagService 애플리케이션 통합테스트")
class TagServiceTest extends IntegrationTestSupport {

    @Autowired
    TagService tagService;

    @DisplayName("[조회/실패]저장되지 않은 태그이름으로, 태그 검색시, 예외를 던진다")
    @Test
    void givenUnsavedTagInfo_whenSearchingTag_thenThrowsException() {
        // given
        String unsavedTagName = "unsavedTagName";

        // when // then
        assertThatThrownBy(() -> tagService.searchTag(unsavedTagName))
            .isInstanceOf(TagNotFoundException.class);
    }
}