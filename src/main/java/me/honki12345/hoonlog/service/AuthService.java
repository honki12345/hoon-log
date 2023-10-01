package me.honki12345.hoonlog.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.RefreshToken;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.TokenDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.repository.RefreshTokenRepository;
import me.honki12345.hoonlog.security.jwt.provider.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDTO createTokens(UserAccountDTO dto) {
        List<String> roles = dto.roles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.createAccessToken(dto.id(), dto.username(), roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(dto.id(), dto.username(), roles);
        RefreshToken refreshTokenEntity = RefreshToken.of(dto.id(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        return TokenDTO.of(accessToken, refreshToken);
    }
}
