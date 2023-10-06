package me.honki12345.hoonlog.service;


import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.dto.TagDTO;
import me.honki12345.hoonlog.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Set<TagDTO> saveTags(Set<TagDTO> tagDTOs) {
        Set<Tag> tags = tagDTOs.stream().map(TagDTO::toEntity)
            .collect(Collectors.toUnmodifiableSet());
        return tags.stream().map(tagRepository::save).map(TagDTO::fromWithoutPostIds)
            .collect(Collectors.toUnmodifiableSet());
    }
}
