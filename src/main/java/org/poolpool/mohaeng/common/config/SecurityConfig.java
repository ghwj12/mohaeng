package org.poolpool.mohaeng.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 공격 방어 기능 끄기 (POST 요청을 포스트맨으로 쏘려면 일단 꺼야 합니다)
            .csrf(csrf -> csrf.disable())
            
            // 2. 모든 요청에 대해 권한 검사 없이 묻지도 따지지도 않고 통과!
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
}