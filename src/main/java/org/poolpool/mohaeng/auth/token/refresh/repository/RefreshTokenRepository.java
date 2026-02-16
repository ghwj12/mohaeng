package org.poolpool.mohaeng.auth.token.refresh.repository;

import java.util.Optional;

import org.poolpool.mohaeng.auth.token.refresh.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByUserId(String userId);
    Optional<RefreshTokenEntity> findByUserIdAndTokenValue(String userId, String tokenValue);
    void deleteByUserId(String userId);
}
