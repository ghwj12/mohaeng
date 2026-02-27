package org.poolpool.mohaeng.admin.eventStats.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;

public class AdminEventStatsDto {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EventListResponse {
        private Long eventId;
        private String title;
        private String categoryName;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private String eventStatus;
        private Integer views;
        private String thumbnail;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MonthlyStatsResponse {
        private Integer month;
        private Long count;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryStatsResponse {
        private String categoryName;
        private Long count;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EventAnalysisDetailResponse {
    	
    	private String topicIds;    // ✅ 추가
    	private String hashtagIds;
    	
        private Long eventId;
        private String title;
        private String thumbnail;           // ✅ 썸네일 URL (파일명만 — 프론트에서 경로 조합)
        private String eventPeriod;
        private String location;
        private String simpleExplain;
        private String hashtags;
        // 주최자
        private String hostName;
        private String hostEmail;
        private String hostPhone;
        // 통계
        private Integer viewCount;
        private Integer participantCount;
        private Integer reviewCount;        // TODO: 리뷰 엔티티 연동 시 업데이트
        private Integer wishCount;          // TODO: 위시리스트 엔티티 연동 시 업데이트
        // 수익
        private Integer totalRevenue;
        private Integer participantRevenue; // price * 결제완료 참여자수
        private Integer boothRevenue;       // (totalCount - remainCount) * boothPrice 합산
        // 성별
        private Long maleCount;
        private Long femaleCount;
        // 연령대 { "20대": 5, "30대": 12, ... }
        private Map<String, Long> ageGroupCounts;
    }
}
