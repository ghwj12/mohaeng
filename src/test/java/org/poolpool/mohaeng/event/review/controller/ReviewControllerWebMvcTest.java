package org.poolpool.mohaeng.event.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.poolpool.mohaeng.common.api.GlobalExceptionHandler;
import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.review.dto.EventReviewTabItemDto;
import org.poolpool.mohaeng.event.review.dto.MyPageReviewItemDto;
import org.poolpool.mohaeng.event.review.dto.ReviewCreateRequestDto;
import org.poolpool.mohaeng.event.review.dto.ReviewEditRequestDto;
import org.poolpool.mohaeng.event.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안필터로 401/403 막히는 거 방지(컨트롤러 바인딩 테스트용)
@Import(GlobalExceptionHandler.class)     // 헤더 누락/예외를 500으로 내려주는 전역 예외처리 적용
class ReviewControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ReviewService reviewService;

    private static final String BASE = "/api";
    private static final String USER_HEADER = "userId"; // 너가 지정한 유저 ID 헤더명

    @Test
    void myList_200() throws Exception {
        var data = new PageResponse<MyPageReviewItemDto>(
                List.of(), 0, 10, 0L, 0
        );
        given(reviewService.selectMyList(eq(1L), any(Pageable.class)))
                .willReturn(data);

        mockMvc.perform(get(BASE + "/users/{userId}/reviews", 1L)
                .header(USER_HEADER, "1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().selectMyList(eq(1L), any(Pageable.class));
    }

    @Test
    void eventList_200() throws Exception {
        var data = new PageResponse<EventReviewTabItemDto>(
                List.of(), 0, 10, 0L, 0
        );
        given(reviewService.selectEventReviews(eq(1L), eq(10L), any(Pageable.class)))
                .willReturn(data);

        mockMvc.perform(get(BASE + "/events/{eventId}/reviews", 10L)
                        .header(USER_HEADER, "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().selectEventReviews(eq(1L), eq(10L), any(Pageable.class));
    }

    @Test
    void myReview_200() throws Exception {
        given(reviewService.selectMyReviewForEvent(eq(1L), eq(10L)))
                .willReturn(Optional.empty());

        mockMvc.perform(get(BASE + "/events/{eventId}/reviews/my", 10L)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().selectMyReviewForEvent(1L, 10L);
    }

    @Test
    void create_200() throws Exception {
        given(reviewService.create(eq(1L), any(ReviewCreateRequestDto.class)))
                .willReturn(100L);

        String body = """
                {
                  "eventId": 10,
                  "ratingContent": 5,
                  "ratingProgress": 4,
                  "ratingMood": 5,
                  "content": "좋아요"
                }
                """;

        mockMvc.perform(post(BASE + "/reviews")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().create(eq(1L), any(ReviewCreateRequestDto.class));
    }

    @Test
    void update_200() throws Exception {
        given(reviewService.edit(eq(1L), eq(100L), any(ReviewEditRequestDto.class)))
                .willReturn(true);

        String body = """
                {
                  "ratingContent": 4,
                  "ratingProgress": 3,
                  "ratingMood": 4,
                  "content": "수정했어요"
                }
                """;

        mockMvc.perform(put(BASE + "/reviews/{reviewId}", 100L)
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().edit(eq(1L), eq(100L), any(ReviewEditRequestDto.class));
    }

    @Test
    void delete_200() throws Exception {
        given(reviewService.delete(eq(1L), eq(100L)))
                .willReturn(true);

        mockMvc.perform(delete(BASE + "/reviews/{reviewId}", 100L)
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(reviewService).should().delete(1L, 100L);
    }

    // ✅ 너가 원한대로: 헤더 없으면 500 유지
    @Test
    void header_missing_500() throws Exception {
        mockMvc.perform(get(BASE + "/events/10/me"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("서버 오류"));
    }
}
