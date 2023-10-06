package me.honki12345.hoonlog.dto;

import java.util.Set;
import java.util.stream.Collectors;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.Tag;

public record TagDTO(
    Long id,
    String tagName,
    Set<Long> postIds
) {

    public static TagDTO of(String tagName) {
        return new TagDTO(null, tagName, null);
    }

    public static TagDTO fromWithoutPostIds(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), null);
    }

    public static Set<TagDTO> fromWithoutPostIds(Set<Tag> tags) {
        return tags.stream().map(TagDTO::fromWithoutPostIds).collect(Collectors.toUnmodifiableSet());
    }

    public Tag toEntity() {
        return Tag.of(this.id, this.tagName, null);
    }
}
