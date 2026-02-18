package org.poolpool.mohaeng.auth.service;

import java.time.LocalDateTime;

import org.poolpool.mohaeng.auth.dto.request.LoginRequest;
import org.poolpool.mohaeng.auth.dto.response.TokenResponse;
import org.poolpool.mohaeng.auth.token.jwt.JwtProperties;
import org.poolpool.mohaeng.auth.token.jwt.JwtTokenProvider;
import org.poolpool.mohaeng.auth.token.refresh.service.RefreshTokenService;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
	
	public TokenResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.userId(), req.userPwd())
        );

        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");

        //이메일로 회원 고유ID 찾기
    	UserEntity user = userRepository.findByEmail(email)
    	        .orElseThrow(() -> new RuntimeException("해당 이메일의 회원이 없습니다."));
    	
    	Long userId = user.getUserId();
    	
        String access = jwtTokenProvider.createAccessToken(userId, role);
        String refresh = jwtTokenProvider.createRefreshToken(userId, role);

        LocalDateTime now = LocalDateTime.now();
        refreshTokenService.upsert(userId, refresh, now, now.plusDays(1)); // refresh 1일

        return new TokenResponse(access, refresh, jwtProperties.accessExp(), jwtProperties.refreshExp());
    }
}
