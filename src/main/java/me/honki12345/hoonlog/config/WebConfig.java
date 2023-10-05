package me.honki12345.hoonlog.config;

import java.io.File;
import java.util.List;
import me.honki12345.hoonlog.security.jwt.util.IfLoginArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String UPLOAD_LOCATION =
        System.getProperty("user.home") + File.separator + "hoonlog";
    public static final String UPLOAD_URL = "/images/post/";
    public static final String IMAGE_LOCATION =
        System.getProperty("user.home") + File.separator + "hoonlog"
            + File.separator + "post";

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new IfLoginArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:///" + UPLOAD_LOCATION);
    }
}
