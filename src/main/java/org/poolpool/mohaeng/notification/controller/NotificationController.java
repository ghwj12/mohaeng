package org.poolpool.mohaeng.notification.controller;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.notification.dto.NotificationItemDto;
import org.poolpool.mohaeng.notification.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationItemDto>>> list(
            @RequestHeader(name = "userId") long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        var data = notificationService.getList(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok("내 알림 목록 조회 성공", data));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count(
            @RequestHeader(name = "userId") long userId
    ) {
        long cnt = notificationService.count(userId);
        return ResponseEntity.ok(ApiResponse.ok("알림 개수 조회 성공", cnt));
    }

    // 읽음 = 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> read(
            @RequestHeader(name = "userId") long userId,
            @PathVariable("notificationId") long notificationId
    ) {
        notificationService.read(userId, notificationId);
        return ResponseEntity.ok(ApiResponse.ok("알림 읽음 처리 성공", null));
    }

    // 전체읽음 = 전체삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> readAll(
            @RequestHeader(name = "userId") long userId
    ) {
        notificationService.readAll(userId);
        return ResponseEntity.ok(ApiResponse.ok("전체 알림 읽음 처리 성공", null));
    }
}