package me.honki12345.hoonlog.config;

import static me.honki12345.hoonlog.domain.util.FileUtils.IMAGE_LOCATION;
import static me.honki12345.hoonlog.domain.util.FileUtils.UPLOAD_LOCATION;

import com.thedeanda.lorem.LoremIpsum;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.repository.RoleRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.repository.elasticsearch.PostSearchRepository;
import me.honki12345.hoonlog.repository.jdbc.PostBulkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

@Slf4j
@DependsOn({"fileUtils"})
@Configuration
public class Initializer {

    public static final String DEFAULT_ROLE_NAME = "ROLE_USER";

    @Bean
    public CommandLineRunner init(RoleRepository roleRepository) {

        return args -> {
            if (roleRepository.count() == 0) {
                Role userRole = Role.of(DEFAULT_ROLE_NAME);

                roleRepository.save(userRole);

            }

            File uploadFolder = new File(UPLOAD_LOCATION);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }

            File imageFolder = new File(IMAGE_LOCATION);
            if (!imageFolder.exists()) {
                imageFolder.mkdir();
            }
        };
    }


    @Bean
    @Profile("search-performance-test")
    public CommandLineRunner insertBulkData(RoleRepository roleRepository,
        UserAccountRepository userAccountRepository, PostSearchRepository postSearchRepository,
        PostBulkRepository postBulkRepository) {

        return args -> {
            if (roleRepository.count() == 0) {
                Role userRole = Role.of(DEFAULT_ROLE_NAME);

                roleRepository.save(userRole);

            }

            File uploadFolder = new File(UPLOAD_LOCATION);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }

            File imageFolder = new File(IMAGE_LOCATION);
            if (!imageFolder.exists()) {
                imageFolder.mkdir();
            }

            LoremIpsum lorem = LoremIpsum.getInstance();

            UserAccount userAccount = UserAccount.of("test123", "12345678", "fpg@email.com",
                me.honki12345.hoonlog.domain.vo.Profile.of("name", "bio"));
            UserAccount savedUserAccount = userAccountRepository.save(userAccount);

            List<Post> posts = new LinkedList<>();
            for (int i = 1; i <= 300_000; i++) {
                Post post = Post.of((long) i, savedUserAccount, lorem.getTitle(3, 5), lorem.getWords(5, 10));
                post.updateTimeAndWriter(userAccount.getUsername());
                posts.add(post);

                if (i % 10_000 == 0) {
                    postBulkRepository.batchInsertPosts(posts);
                    postSearchRepository.saveAll(posts);
                    posts.clear();
                }
            }

            log.info("=================All data is inserted================");
        };
    }

}
