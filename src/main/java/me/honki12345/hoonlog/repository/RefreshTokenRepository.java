package me.honki12345.hoonlog.repository;

import java.util.Optional;
import me.honki12345.hoonlog.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
}
