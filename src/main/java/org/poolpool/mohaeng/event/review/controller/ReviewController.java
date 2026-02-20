package org.poolpool.mohaeng.event.review.controller;

import jakarta.validation.Valid;

import java.util.Optional;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.review.dto.EventReviewTabItemDto;
import org.poolpool.mohaeng.event.review.dto.MyPageReviewItemDto;
import org.poolpool.mohaeng.event.review.dto.ReviewCreateRequestDto;
import org.poolpool.mohaeng.event.review.dto.ReviewEditRequestDto;
import org.poolpool.mohaeng.event.review.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ✅ URL 유지: /api/users/{userId}/reviews
    // ✅ userId는 헤더로 통일
    // ✅ path userId는 검증용으로만 사용 (불일치 시 403을 "직접" 반환 -> GlobalExceptionHandler에 의해 500으로 안 바뀜)
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<MyPageReviewItemDto>>> myList(
            @PathVariable("userId") long userIdFromPath,
            @RequestHeader(name = "userId") long userIdFromHeader,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (userIdFromPath != userIdFromHeader) {
            // ✅ 예외 던지지 말고 403 응답 직접 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("요청 userId가 일치하지 않습니다.", null));
        }

        var pageable = PageRequest.of(page, size);
        var data = reviewService.selectMyList(userIdFromHeader, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 리뷰 목록 조회 성공", data));
    }

    @GetMapping("/events/{eventId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<EventReviewTabItemDto>>> eventList(
            @RequestHeader(name = "userId") long userId,
            @PathVariable("eventId") long eventId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size);
        var data = reviewService.selectEventReviews(userId, eventId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("이벤트 리뷰 목록 조회 성공", data));
    }

    @GetMapping("/events/{eventId}/reviews/my")
    public ResponseEntity<ApiResponse<Optional<EventReviewTabItemDto>>> myReview(
            @RequestHeader(name = "userId") long userId,
            @PathVariable("eventId") long eventId
    ) {
        var data = reviewService.selectMyReviewForEvent(userId, eventId);
        return ResponseEntity.ok(ApiResponse.ok("내 리뷰 조회 성공", data));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<Void>> create(
            @RequestHeader(name = "userId") long userId,
            @Valid @RequestBody ReviewCreateRequestDto request
    ) {
        reviewService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("리뷰 작성 성공", (Void) null));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestHeader(name = "userId") long userId,
            @PathVariable("reviewId") long reviewId,
            @Valid @RequestBody ReviewEditRequestDto request
    ) {
        reviewService.edit(userId, reviewId, request);
        return ResponseEntity.ok(ApiResponse.ok("리뷰 수정 성공", (Void) null));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader(name = "userId") long userId,
            @PathVariable("reviewId") long reviewId
    ) {
        reviewService.delete(userId, reviewId);
        return ResponseEntity.ok(ApiResponse.ok("리뷰 삭제 성공", (Void) null));
    }
}