package me.honki12345.hoonlog.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostImage;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.PostDTO;
import me.honki12345.hoonlog.dto.PostImageDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostImageRepository;
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
    private final PostImageRepository postImageRepository;

    @Transactional(readOnly = true)
    public Page<PostDTO> searchPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDTO::from)
            .map(postDTO -> postDTO.addPostImageDTOList(
                PostImageDTO.from(postImageRepository.findAllByPostId(
                    postDTO.id()))));

    }

    public PostDTO addPost(PostDTO postDTO,
        List<MultipartFile> postImageFileList,
        UserAccountDTO userAccountDTO) {
        UserAccount userAccount = userAccountRepository.findById(userAccountDTO.id())
            .orElseThrow(() -> new UserAccountNotFoundException(
                ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postDTO.toEntity();
        post.addUserAccount(userAccount);

        if (postImageFileList != null) {
            for (MultipartFile multipartFile : postImageFileList) {
                PostImage postImage = new PostImage();
                postImage.addPost(post);
                postImageService.savePostImage(postImage, multipartFile);
            }
        }

        return PostDTO.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public PostDTO searchPost(Long postId) {
        PostDTO postDTO = PostDTO.from(postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND)));

        List<PostImage> postImageList = postImageRepository.findAllByPostId(postId);

        if (!postImageList.isEmpty()) {
            postDTO.addPostImageDTOList(PostImageDTO.from(postImageList));
        }

        return postDTO;
    }

    public PostDTO updatePost(Long postId, UserAccountDTO userAccountDTO,
        PostDTO postDTO, List<MultipartFile> postImageFileList) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));
        if (!post.getUserAccount().getUsername().equals(userAccountDTO.username())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        post.updateTitleAndContent(postDTO.title(), postDTO.content());

        List<Long> postImageIds = postDTO.postImageIds();
        if (postImageFileList != null) {
            for (int i = 0; i < postImageFileList.size(); i++) {
                postImageService.updatePostImage(postImageIds.get(i), postImageFileList.get(i));
            }
        }

        return PostDTO.from(post);
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
