package me.honki12345.hoonlog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "hoonlog API 명세서",
        description = "벨로그를 모티브로한 블로그플랫폼 서비스 API 명세서",
        version = "v1")
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi hoonlogOpenApi() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
            .group("hoonlog API v1")
            .pathsToMatch(paths)
            .build();
    }

}
