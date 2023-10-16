package me.honki12345.hoonlog.util;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.TagRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TagBuilder {

    public static final String TEST_TAG_NAME = "tagName";

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public void deleteAllInBatch() {
        this.tagRepository.deleteAllInBatch();
    }

    public void createTagByTestUser(Post post) {
        Post findPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        Tag tag = Tag.of(TEST_TAG_NAME);
        findPost.addTags(Set.of(tag));
        tagRepository.save(tag);
    }
}
