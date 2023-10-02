package me.honki12345.hoonlog.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.RefreshToken;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.LogoutErrorException;
import me.honki12345.hoonlog.error.exception.NotFoundException;
import me.honki12345.hoonlog.error.exception.UserAccountNotFoundException;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.security.jwt.util.JwtTokenizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;

    public TokenDTO createTokens(UserAccountDTO dto) {
        List<String> roles = dto.roles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenizer.createAccessToken(dto.id(), dto.username(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(dto.id(), dto.username(), roles);
        RefreshToken refreshTokenEntity = RefreshToken.of(dto.id(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        return TokenDTO.of(accessToken, refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new LogoutErrorException(ErrorCode.LOGOUT_ERROR));
        refreshTokenRepository.deleteById(refreshTokenEntity.getId());
    }


    public TokenDTO refreshAccessToken(String refreshToken) {
        TokenDTO tokenDTO = refreshTokenRepository.findByToken(refreshToken)
            .map(TokenDTO::from)
            .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        Long userIdFromRefreshToken = jwtTokenizer.getUserIdFromRefreshToken(
            tokenDTO.refreshToken());
        if (!userAccountRepository.existsById(userIdFromRefreshToken)) {
            throw new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND);
        }

        String newAccessToken = jwtTokenizer.createNewAccessToken(refreshToken);
        return TokenDTO.of(newAccessToken, refreshToken);
    }

    public Long FindUserIdByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
            .map(RefreshToken::getUserId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
    }
}
