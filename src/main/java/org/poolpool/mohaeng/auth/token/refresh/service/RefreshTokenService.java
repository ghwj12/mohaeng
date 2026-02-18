package org.poolpool.mohaeng.auth.token.refresh.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.poolpool.mohaeng.auth.token.refresh.entity.RefreshTokenEntity;
import org.poolpool.mohaeng.auth.token.refresh.repository.RefreshTokenRepository;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepository;

    @Transactional
    public RefreshTokenEntity upsert(Long userId, String tokenValue, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        
    	
    	RefreshTokenEntity entity = repo.findByUserId(userId)
                .orElseGet(() -> RefreshTokenEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .build());

        entity.setTokenValue(tokenValue);
        entity.setIssuedAt(issuedAt);
        entity.setExpiresAt(expiresAt);
        entity.setRevoked(false);

        return repo.save(entity);
    }

    @Transactional(readOnly = true)
    public boolean matches(String email, String tokenValue) {
    	//이메일로 회원 고유ID 찾기
    	UserEntity user = userRepository.findByEmail(email)
    	        .orElseThrow(() -> new RuntimeException("해당 이메일의 회원이 없습니다."));
    	
    	Long userId = user.getUserId();
    	
        return repo.findByUserIdAndTokenValue(userId, tokenValue).isPresent();
    }

    @Transactional
    public void deleteByUserId(String email) {
    	//이메일로 회원 고유ID 찾기
    	UserEntity user = userRepository.findByEmail(email)
    	        .orElseThrow(() -> new RuntimeException("해당 이메일의 회원이 없습니다."));
    	
    	Long userId = user.getUserId();
    	
        repo.deleteByUserId(userId);
    }
}
