package me.honki12345.hoonlog.util;

import static me.honki12345.hoonlog.domain.util.FileUtils.UPLOAD_URL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.PostComment;
import me.honki12345.hoonlog.domain.Tag;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.PostCommentRequest;
import me.honki12345.hoonlog.dto.request.PostRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.PostNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.PostCommentRepository;
import me.honki12345.hoonlog.repository.PostImageRepository;
import me.honki12345.hoonlog.repository.PostLikeRepository;
import me.honki12345.hoonlog.repository.PostRepository;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.TagRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.AuthService;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@TestComponent
@RequiredArgsConstructor
public class TestUtils {

    public static final String TEST_USERNAME = "fpg123";
    public static final String TEST_PASSWORD = "12345678";
    public static final String TEST_POST_TITLE = "title";
    public static final String TEST_POST_CONTENT = "content";
    public static final String TEST_UPDATED_POST_TITLE = "updatedTitle";
    public static final String TEST_UPDATED_POST_CONTENT = "updatedContent";
    public static final String TEST_COMMENT_CONTENT = "commentContent";
    public static final String TEST_TAG_NAME = "tagName";
    public static final String TEST_FILE_ORIGINAL_NAME = "drawing.jpg";

    private final AuthService authService;
    private final UserAccountService userAccountService;

    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImageRepository postImageRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;

    public void deleteAllInBatchInAllRepository() {
        this.postLikeRepository.deleteAllInBatch();
        this.postImageRepository.deleteAllInBatch();
        this.postCommentRepository.deleteAllInBatch();
        this.postRepository.deleteAllInBatch();
        this.refreshTokenRepository.deleteAllInBatch();
        this.userAccountRepository.deleteAllInBatch();
        this.tagRepository.deleteAllInBatch();
    }

    public TokenDTO createTokens(UserAccountDTO userAccountDTO) {
        return authService.createTokens(userAccountDTO);
    }

    public TokenDTO createTokensAfterSavingTestUser() {
        UserAccountDTO userAccountDTO = saveTestUser();
        return authService.createTokens(userAccountDTO);
    }

    public UserAccountDTO saveTestUser() {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(TEST_USERNAME, TEST_PASSWORD, "fpg123@mail.com", profileDTO);
    }


    public UserAccountDTO saveTestUser(String username, String password) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, "fpg123@mail.com", profileDTO);
    }

    public UserAccountDTO saveTestUser(String username, String password, String email) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, email, profileDTO);
    }


    public UserAccountDTO saveTestUser(String username, String password, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        return userAccountService.saveUserAccount(request.toDTO());
    }

    public Post createPostByTestUser() {
        PostRequest postRequest = PostRequest.of(TEST_POST_TITLE, TEST_POST_CONTENT);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.saveAndFlush(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }

    public Post createPostByTestUser(String title, String content) {
        PostRequest postRequest = PostRequest.of(title, content);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUsername(
            TEST_USERNAME);
        return optionalUserAccount.map(userAccount -> postRepository.save(
            postRequest.toDTO().toEntity().addUserAccount(userAccount))).orElse(null);
    }

    public PostComment createCommentByTestUser(Long postId) {
        PostCommentRequest postCommentRequest = PostCommentRequest.of(
            TestUtils.TEST_COMMENT_CONTENT);
        PostComment postComment = postCommentRequest.toDTO().toEntity();

        UserAccount userAccount = userAccountRepository.findByUsername(TEST_USERNAME)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
            ErrorCode.POST_NOT_FOUND));

        postComment.addPost(post);
        postComment.addUserAccount(userAccount);
        return postCommentRepository.save(postComment);
    }

    public Tag createTagByTestUser(Post post) {
        Post findPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
        Tag tag = Tag.of(TEST_TAG_NAME);
        findPost.addTags(Set.of(tag));
        return tagRepository.save(tag);
    }

    public String createImageFilePath() {
        StringJoiner sj = new StringJoiner(File.separator);
        String pathname = System.getProperty("user.dir") + File.separator + "src";
        return sj.add(pathname).add("test").add("data").add(TEST_FILE_ORIGINAL_NAME)
            .toString();
    }

    public MultipartFile createMockMultipartFile(String imageFileName) {

        String path = UPLOAD_URL + File.separator;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(path, imageFileName,
            "image/jpg", new byte[]{1, 2, 3, 4});

        return mockMultipartFile;
    }

    public List<MultipartFile> createMockMultipartFiles() {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = UPLOAD_URL + File.separator;
            String imageName = "image" + i + ".jpg";
            MockMultipartFile mockMultipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(mockMultipartFile);
        }

        return multipartFileList;
    }
}
