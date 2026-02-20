package org.poolpool.mohaeng.auth.security.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.poolpool.mohaeng.auth.token.jwt.JwtProperties;
import org.poolpool.mohaeng.auth.token.jwt.JwtTokenProvider;
import org.poolpool.mohaeng.auth.token.refresh.service.RefreshTokenService;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper om = new ObjectMapper();
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;
	private final JwtProperties jwtProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

		String email = oauthUser.getAttribute("email");

		// 이메일로 회원 고유ID 찾기
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 회원이 없습니다. 이메일 : " + email));

		Long userId = user.getUserId();

		String access = jwtTokenProvider.createAccessToken(userId, "ROLE_" + user.getUserRole());
		String refresh = jwtTokenProvider.createRefreshToken(userId, "ROLE_" + user.getUserRole());

		LocalDateTime now = LocalDateTime.now();
		refreshTokenService.upsert(userId, refresh, now, now.plusDays(1)); // refresh 1일
		
		Map<String, Object> body = Map.of("accessToken", access, "refreshToken", refresh, "accessExp",
				jwtProperties.accessExp(), "refreshExp", jwtProperties.refreshExp());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		om.writeValue(response.getWriter(), body);

	}
}
