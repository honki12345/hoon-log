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
import me.honki12345.hoonlog.error.exception.domain.TokenExpiredException;
import me.honki12345.hoonlog.error.exception.domain.TokenIllegalStateException;
import me.honki12345.hoonlog.error.exception.domain.TokenInvalidException;
import me.honki12345.hoonlog.error.exception.domain.TokenUnsupportedException;
import me.honki12345.hoonlog.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // TODO application
    public static final String PREFIX = "Bearer";
    public static final String WHITESPACE = " ";

    private final AuthenticationManager authenticationManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
        String token = "";
        try {
            token = getToken(request);
            getAuthentication(token);
        } catch (IllegalStateException exception) {
            request.setAttribute("exception", new TokenIllegalStateException(TOKEN_INVALID));
            log.error("Token Not Found Exception: {}", token);
        } catch (MalformedJwtException exception) {
            log.error("Invalid Token Exception: {}", token);
            request.setAttribute("exception", new TokenInvalidException(TOKEN_INVALID));
        } catch (ExpiredJwtException exception) {
            log.error("Expired Token Exception: {}", token);
            request.setAttribute("exception", new TokenExpiredException(TOKEN_EXPIRED));
        } catch (UnsupportedJwtException exception) {
            log.error("UnSupported Token Exception: {}", token);
            request.setAttribute("exception", new TokenUnsupportedException(TOKEN_UNSUPPORTED));
        } catch (Exception exception) {
            log.error("JwtFilter - doFilterInternal() Exception");
            log.error("Exception Message: {}", exception.getMessage());
            request.setAttribute("exception", new Exception(exception.getMessage()));
        }
        filterChain.doFilter(request, response);

    }

    private void getAuthentication(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(PREFIX)) {
            throw new IllegalStateException();

        }
        String[] arr = authorization.split(WHITESPACE);
        return arr[1];
    }
}
