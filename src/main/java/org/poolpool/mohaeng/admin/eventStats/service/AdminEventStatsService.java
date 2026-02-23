package org.poolpool.mohaeng.admin.eventStats.service;

import org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto;
import java.time.LocalDate;
import java.util.List;

public interface AdminEventStatsService {
    // 필터링 검색
    List<AdminEventStatsDto.EventListResponse> getAllEvent(LocalDate start, LocalDate end, String category, String location, String status);
    
    // 월별 통계
    List<AdminEventStatsDto.MonthlyStatsResponse> getEventCountByMonth(int year);
    
    // 카테고리 통계
    List<AdminEventStatsDto.CategoryStatsResponse> getEventCountByCategory();
    
    // 단일 행사 분석 조회
    AdminEventStatsDto.EventAnalysisDetailResponse getEventAnalysis(Long eventId);
}