package me.honki12345.hoonlog.security.jwt.util;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import me.honki12345.hoonlog.config.WebConfig;
import me.honki12345.hoonlog.controller.PostController;
import me.honki12345.hoonlog.controller.TestController;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import me.honki12345.hoonlog.service.PostService;
import me.honki12345.hoonlog.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("MockMvcTest")
@Import({WebConfig.class})
@WebMvcTest(
    controllers = {PostController.class, TestController.class},
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)},
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class IfLoginArgumentResolverTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    PostService postService;
    @MockBean
    TagService tagService;

    @DisplayName("Authentication 객체가 null이라면, 권한이 필요한 요청시, 에러 메시지를 반환한다")
    @Test
    void givenAuthenticationIsNull_whenRequiringAuthenticated_thenReturnsErrorMessage()
        throws Exception {
        // given
        SecurityContextHolder.getContext().setAuthentication(null);
        long postId = 999L;
        doNothing().when(postService).deletePost(postId, null);

        // when // then
        mockMvc.perform(
                delete("/api/v1/posts/{postId}", postId))
            .andExpect(status().isInternalServerError());
    }

    @DisplayName("Principal 객체가 null 이라면, 권한이 필요한 요청시, 에러 메세지를 반환한다.")
    @Test
    void givenAuthenticationIsNotInstanceOfToken_whenRequiringAuthenticated_thenReturnsErrorMessage()
        throws Exception {
        // given
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(
                List.of(new SimpleGrantedAuthority("role")), null, null)
        );
        long postId = 999L;
        doNothing().when(postService).deletePost(postId, null);

        // when // then
        mockMvc.perform(
                delete("/api/v1/posts/{postId}", postId))
            .andExpect(status().isInternalServerError());
    }

    @DisplayName("Principal 객체가 null 이라면, 권한이 필요한 요청시, 에러 메세지를 반환한다.")
    @Test
    void givenInvalidClassForAnnotation_whenRequesting_thenReturnsNull()
        throws Exception {
        // given // when // then
        mockMvc.perform(
                get("/test"))
            .andDo(print());
    }
}