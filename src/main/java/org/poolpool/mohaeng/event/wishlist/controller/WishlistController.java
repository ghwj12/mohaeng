package org.poolpool.mohaeng.event.wishlist.controller;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistCreateRequestDto;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistItemDto;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistToggleRequestDto;
import org.poolpool.mohaeng.event.wishlist.exception.WishlistAlreadyExistsException;
import org.poolpool.mohaeng.event.wishlist.exception.WishlistNotFoundOrForbiddenException;
import org.poolpool.mohaeng.event.wishlist.service.EventWishlistService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WishlistController {

    private final EventWishlistService wishlistService;

    public WishlistController(EventWishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    private long currentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

    // 관심행사 등록(하트 ON)  ✅ URL 유지
    @PostMapping("/users/{userId}/wishlist")
    public ResponseEntity<ApiResponse<Long>> create(
            @PathVariable("userId") long userId,  // ✅ name 명시
            @RequestBody WishlistCreateRequestDto request,
            Authentication authentication
    ) {
        long loginUserId = currentUserId(authentication);

        // (선택) URL userId와 로그인 userId 불일치 방어
        if (userId != loginUserId) {
            return ResponseEntity.status(403).body(ApiResponse.fail("권한이 없습니다.", null));
        }

        try {
            long wishId = wishlistService.add(loginUserId, request);
            return ResponseEntity.ok(ApiResponse.ok("관심행사 등록 완료", wishId));
        } catch (WishlistAlreadyExistsException e) {
            return ResponseEntity.status(409).body(ApiResponse.fail(e.getMessage(), null));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(ApiResponse.fail("이미 관심 등록된 행사입니다.", null));
        }
    }

    // 마이페이지: 관심행사 목록(최신순) ✅ URL 유지
    @GetMapping("/users/{userId}/wishlist")
    public ResponseEntity<ApiResponse<PageResponse<WishlistItemDto>>> list(
            @PathVariable("userId") long userId,  // ✅ name 명시
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication authentication
    ) {
        long loginUserId = currentUserId(authentication);

        if (userId != loginUserId) {
            return ResponseEntity.status(403).body(ApiResponse.fail("권한이 없습니다.", null));
        }

        int safePage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(safePage - 1, size);

        PageResponse<WishlistItemDto> data = wishlistService.getList(loginUserId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("관심행사 목록 조회 성공", data));
    }

    // 알림 아이콘 토글(ON/OFF) ✅ URL 유지
    @PutMapping("/wishlist/{wishId}/notification")
    public ResponseEntity<ApiResponse<WishlistItemDto>> toggleNotification(
            @PathVariable("wishId") long wishId,  // ✅ name 명시
            @RequestBody WishlistToggleRequestDto request,
            Authentication authentication
    ) {
        long loginUserId = currentUserId(authentication);

        try {
            WishlistItemDto changed = wishlistService.toggleNotification(loginUserId, wishId, request);
            return ResponseEntity.ok(ApiResponse.ok("알림 설정 변경 완료", changed));
        } catch (WishlistNotFoundOrForbiddenException e) {
            return ResponseEntity.status(404).body(ApiResponse.fail(e.getMessage(), null));
        }
    }

    // 관심행사 해제(하트 OFF) ✅ URL 유지
    @DeleteMapping("/wishlist/{wishId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("wishId") long wishId,  // ✅ name 명시
            Authentication authentication
    ) {
        long loginUserId = currentUserId(authentication);

        try {
            wishlistService.remove(loginUserId, wishId);
            return ResponseEntity.ok(ApiResponse.ok("관심행사 해제 완료", null));
        } catch (WishlistNotFoundOrForbiddenException e) {
            return ResponseEntity.status(404).body(ApiResponse.fail(e.getMessage(), null));
        }
    }
}