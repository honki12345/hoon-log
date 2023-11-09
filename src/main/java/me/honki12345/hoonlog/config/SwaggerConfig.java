package me.honki12345.hoonlog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

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
            .addOpenApiCustomizer(openApi -> openApi.setServers(
                List.of(new Server().description("prod").url("https://hoon-log.p-e.kr"),
                    new Server().description("dev").url("http://localhost:8080"))))
            .build();
    }

}
