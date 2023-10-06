package me.honki12345.hoonlog.service;


import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.dto.TagDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.TagNotFoundException;
import me.honki12345.hoonlog.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Tag searchTag(String tagName) {
        return tagRepository.findByName(tagName)
            .orElseThrow(() -> new TagNotFoundException(ErrorCode.TAG_NOT_FOUND));
    }

    public Tag getTagIfPresent(String tagName) {
        return tagRepository.findByName(tagName).orElse(Tag.of(tagName));
    }
}
