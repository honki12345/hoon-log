package me.honki12345.hoonlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Tag;

@Schema(description = "태그 DTO")
public record TagDTO(
    @Schema(description = "태그 번호")
    Long id,
    @Schema(description = "태그 이름")
    String tagName,
    @Schema(description = "(태그가 속한)게시물 아이디들")
    Set<Long> postIds
) {

    public static TagDTO fromWithoutPostIds(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), null);
    }

    public static Set<TagDTO> fromWithoutPostIds(Set<Tag> tags) {
        return tags.stream().map(TagDTO::fromWithoutPostIds)
            .collect(Collectors.toUnmodifiableSet());
    }
}
