package org.poolpool.mohaeng.user.repository;

import java.util.Optional;

import org.poolpool.mohaeng.user.entity.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUserEntity, Long> {

    // 특정 소셜 계정 조회
    Optional<SocialUserEntity> findByProviderAndProviderId(String provider, String providerId);

    // 한 유저가 가진 모든 소셜 계정 조회
//    List<SocialUserEntity> findByUser(UserEntity user);
}

