package org.poolpool.mohaeng.admin.eventStats.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto;
import org.poolpool.mohaeng.admin.eventStats.service.AdminEventStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/eventstats")
@RequiredArgsConstructor
public class AdminEventStatsController {

    private final AdminEventStatsService service;

    // 1. 전체 행사 분석 조회
    @GetMapping("/getAllEvent")
    public ResponseEntity<List<AdminEventStatsDto.EventListResponse>> getAllEvent(
            // 👇 여기에 name = "..." 을 추가했습니다! 👇
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "status", required = false) String status
    ) {
        return ResponseEntity.ok(service.getAllEvent(startDate, endDate, category, location, status));
    }

    // 2. 월별 행사 수 조회
    @GetMapping("/getEventCountByMonth")
    public ResponseEntity<List<AdminEventStatsDto.MonthlyStatsResponse>> getEventCountByMonth(
            // 👇 여기도 name = "year" 를 추가했습니다! 👇
            @RequestParam(name = "year", defaultValue = "2026") int year
    ) {
        return ResponseEntity.ok(service.getEventCountByMonth(year));
    }

    // 3. 카테고리 행사 수 조회
    @GetMapping("/getEventCountByCategory")
    public ResponseEntity<List<AdminEventStatsDto.CategoryStatsResponse>> getEventCountByCategory() {
        return ResponseEntity.ok(service.getEventCountByCategory());
    }
    
    // 4. 단일 행사 분석 조회
    @GetMapping("/getEventAnalysis/{eventId}")
    public ResponseEntity<AdminEventStatsDto.EventAnalysisDetailResponse> getEventAnalysis(
            @PathVariable(name = "eventId") Long eventId
    ) {
        return ResponseEntity.ok(service.getEventAnalysis(eventId));
    }
}