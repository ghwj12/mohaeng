package org.poolpool.mohaeng.admin.eventStats.dto;

import lombok.*;
import java.time.LocalDate;

public class AdminEventStatsDto {

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class EventListResponse {
        private Long eventId;
        private String title;
        private String categoryName; // Category 객체에서 이름만 추출
        private String location;     // lotNumberAdr 사용
        private String eventStatus;  // eventStatus
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer views;       // 조회수 (Integer)
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class MonthlyStatsResponse {
        private int month; 
        private long count;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class CategoryStatsResponse {
        private String categoryName; 
        private long count;
    }
    
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class EventAnalysisDetailResponse {
        // --- 1. 행사 기본 정보 ---
        private String title;
        private String simpleExplain;
        private String eventPeriod;  // "2026-06-01 ~ 2026-06-03"
        private String location;     // lotNumberAdr 또는 detailAdr
        private String hostName;
        private String hostEmail;
        private String hostPhone;
        private String hashtags;

        // --- 2. 통계 지표 ---
        private long viewCount;          // 조회수
        private long participantCount;   // 참여자 수 (결제완료 기준)
        private long reviewCount;        // 리뷰 수
        private long wishCount;          // 관심(찜) 수
        private long totalRevenue;       // 총 수익 (참여 수익 + 부스 수익)

        // --- 3. 참여자 성별 비율 ---
        private long maleCount;          // 남성 참여자 수
        private long femaleCount;        // 여성 참여자 수
    }
}