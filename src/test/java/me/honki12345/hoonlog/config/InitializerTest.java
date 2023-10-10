package me.honki12345.hoonlog.config;

import static me.honki12345.hoonlog.domain.util.FileUtils.IMAGE_LOCATION;
import static me.honki12345.hoonlog.domain.util.FileUtils.UPLOAD_LOCATION;
import static org.assertj.core.api.Assertions.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.repository.RoleRepository;
import me.honki12345.hoonlog.util.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Initializer 테스트")
@Import({TestUtils.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InitializerTest {

    @Autowired
    CommandLineRunner commandLineRunner;
    @Autowired
    RoleRepository roleRepository;

    @DisplayName("commandLineRunner 테스트")
    @Test
    void givenNothingInRoleRepository_whenCommandLineRunner_thenCreatingRole() throws Exception {
        // given
        File uploadFolder = new File(UPLOAD_LOCATION);
        File imageFolder = new File(IMAGE_LOCATION);
        if (imageFolder.exists()) {
            imageFolder.delete();
        }
        if (uploadFolder.exists()) {
            uploadFolder.delete();
        }
        roleRepository.deleteAllInBatch();

        // when
        commandLineRunner.run(null);

        // then
        assertThat(roleRepository.count()).isGreaterThan(0L);
    }

    @DisplayName("")
    @Test
    void givenRole_when() throws Exception {
        // given
        Role hello = roleRepository.save(Role.of("hello"));

        // when
        commandLineRunner.run(null);

        // then
        assertThat(roleRepository.count()).isGreaterThan(0L);
        roleRepository.delete(hello);
    }

}