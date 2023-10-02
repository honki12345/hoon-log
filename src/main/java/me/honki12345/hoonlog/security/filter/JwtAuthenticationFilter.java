package me.honki12345.hoonlog.security.filter;

import static me.honki12345.hoonlog.error.ErrorCode.*;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.honki12345.hoonlog.error.exception.JwtException;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String PREFIX = "Bearer";
    public static final String WHITESPACE = " ";

    private final AuthenticationManager authenticationManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
        String token = "";
        try {
            token = getToken(request);
            if (StringUtils.hasText(token)) {
                getAuthentication(token);
            }
        } catch (NullPointerException | IllegalStateException exception) {
            request.setAttribute("exception", new JwtException(TOKEN_NOT_FOUND));
            log.error("Token Not Found Exception: {}", token);
        } catch (MalformedJwtException exception) {
            log.error("Invalid Token Exception: {}", token);
            request.setAttribute("exception", new JwtException(TOKEN_INVALID));
        } catch (ExpiredJwtException exception) {
            log.error("Expired Token Exception: {}", token);
            request.setAttribute("exception", new JwtException(TOKEN_EXPIRED));
        } catch (UnsupportedJwtException exception) {
            log.error("UnSupported Token Exception: {}", token);
            request.setAttribute("exception", new JwtException(TOKEN_UNSUPPORTED));
        } catch (Exception exception) {
            log.error("JwtFilter - doFilterInternal() Exception");
            log.error("Exception Message: {}", exception.getMessage());
            request.setAttribute("exception", new Exception(exception.getMessage()));
        }
        filterChain.doFilter(request, response);

    }

    private void getAuthentication(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith(PREFIX)) {
            String[] arr = authorization.split(WHITESPACE);
            return arr[1];
        }
        return null;
    }
}
