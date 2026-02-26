package org.poolpool.mohaeng.event.mypage.controller;

import java.util.List;

import org.poolpool.mohaeng.auth.security.principal.CustomUserPrincipal;
import org.poolpool.mohaeng.event.mypage.dto.BoothMypageResponse;
import org.poolpool.mohaeng.event.mypage.service.MypageBoothService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/events")
public class MypageBoothController {

    private final MypageBoothService service;

    /**
     * ✅ 문의 컨트롤러와 동일한 방식으로 현재 로그인 사용자 ID 추출
     */
    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new IllegalStateException("인증 principal이 없습니다.");
        }

        if (principal instanceof CustomUserPrincipal cup) {
            if (cup.getUserId() == null) throw new IllegalStateException("principal userId가 null 입니다.");
            return Long.valueOf(cup.getUserId());
        }

        if (principal instanceof UserDetails ud) {
            String username = ud.getUsername();
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException ignored) {
                throw new IllegalStateException("principal이 UserDetails(username=" + username + ") 입니다. email->userId 변환 로직이 필요합니다.");
            }
        }

        if (principal instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                throw new IllegalStateException("principal이 String(" + s + ") 입니다. email->userId 변환 로직이 필요합니다.");
            }
        }

        throw new IllegalStateException("지원하지 않는 principal 타입: " + principal.getClass().getName());
    }

    /**
     * ✅ 부스 관리(내 신청 내역) - 토큰 기반
     */
    @GetMapping("/booths")
    public ResponseEntity<List<BoothMypageResponse>> myBooths() {
        Long uid = currentUserId();
        return ResponseEntity.ok(service.getMyBooths(uid));
    }
}
