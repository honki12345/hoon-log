package me.honki12345.hoonlog.config;

import static me.honki12345.hoonlog.config.WebConfig.UPLOAD_LOCATION;

import java.io.File;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Initializer {



    @Bean
    public CommandLineRunner init(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role userRole = Role.of("ROLE_USER");

                roleRepository.save(userRole);

            }

            File folder = new File(UPLOAD_LOCATION);
            if (!folder.exists()) {
                folder.mkdir();
            }
        };
    }

}
