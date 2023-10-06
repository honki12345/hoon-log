package me.honki12345.hoonlog.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostImageService postImageService;
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Post searchPost(Long postId) {
        return postRepository.findByPostIdWithAll(postId)
            .orElseThrow(() -> new PostNotFoundException(
                ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Post> searchPosts(Pageable pageable) {
        return postRepository.findAllWithAll(pageable);
    }

    public Post addPost(PostDTO postDTO,
        List<MultipartFile> postImageFiles,
        Set<String> tagNames, UserAccountDTO userAccountDTO) {
        UserAccount userAccount = userAccountRepository.findById(userAccountDTO.id())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));

        Post post = postDTO.toEntity();
        post.addUserAccount(userAccount);
        Optional.ofNullable(postImageFiles).ifPresent(
            multipartFiles -> postImageService.savePostImagesWithPost(postImageFiles, post));
        post.addTags(tagNames.isEmpty() ?
            Collections.emptySet() : tagNames.stream().map(Tag::of).collect(
            Collectors.toUnmodifiableSet()));

        return postRepository.save(post);
    }

    public Post updatePost(Long postId, UserAccountDTO userAccountDTO,
        PostDTO postDTO, List<MultipartFile> postImageFiles,
        Set<String> tagNames) {
        Post post = postRepository.findByPostIdWithAll(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        if (!post.getUserAccount().getUsername().equals(userAccountDTO.username())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        post.updateTitleAndContent(postDTO.title(), postDTO.content());

        Set<Tag> tags =
            tagNames.isEmpty() ? Collections.emptySet() : tagNames.stream().map(Tag::of).collect(
                Collectors.toUnmodifiableSet());
        post.updateTags(tags);

        List<Long> postImageIds = postDTO.postImageIds();
        if (postImageFiles != null) {
            for (int i = 0; i < postImageFiles.size(); i++) {
                postImageService.updatePostImage(postImageIds.get(i), postImageFiles.get(i));
            }
        }

        return post;
    }


    public void deletePost(Long postId, UserAccountDTO dto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        if (!post.getUserAccount().getUsername().equals(dto.username())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        postRepository.deleteById(postId);
    }
}
