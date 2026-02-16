package org.poolpool.mohaeng.auth.security.filter;

import java.io.IOException;

import org.poolpool.mohaeng.auth.dto.response.ErrorResponse;
import org.poolpool.mohaeng.auth.exception.AuthException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationFilter에서 발생하는 예외를 JSON으로 변환해주는 필터
 * - 반드시 JwtAuthenticationFilter "앞"에 둔다.
 */
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            response.setStatus(e.getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            om.writeValue(response.getWriter(),
                    ErrorResponse.of(e.getCode(), e.getMessage(), request.getRequestURI())
            );
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            om.writeValue(response.getWriter(),
                    ErrorResponse.of("AUTH_ERROR", "인증 처리 중 오류가 발생했습니다.", request.getRequestURI())
            );
        }
    }
}
