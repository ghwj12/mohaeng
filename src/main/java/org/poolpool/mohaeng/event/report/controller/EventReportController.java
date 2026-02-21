package org.poolpool.mohaeng.event.report.controller;

import jakarta.validation.Valid;

import org.poolpool.mohaeng.admin.report.dto.AdminReportCreateRequestDto;
import org.poolpool.mohaeng.admin.report.service.AdminReportService;
import org.poolpool.mohaeng.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class EventReportController {

    private final AdminReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(
        @RequestHeader(name = "userId") long reporterId,
        @Valid @RequestBody AdminReportCreateRequestDto request
    ) {
        reportService.create(reporterId, request);
        return ResponseEntity.ok(ApiResponse.ok("이벤트 신고 등록 성공", null));
    }
}