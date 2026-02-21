package org.poolpool.mohaeng.admin.report.dto;

import java.time.LocalDateTime;

import org.poolpool.mohaeng.admin.report.entity.AdminReportFEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReportListItemDto {
    private Long reportId;
    private Long eventId;
    private String eventName;
    private String reasonCategory;
    private LocalDateTime createdAt;
    private String reportResult;

    public static AdminReportListItemDto fromEntity(AdminReportFEntity r, String eventName) {
        return AdminReportListItemDto.builder()
            .reportId(r.getReportId())
            .eventId(r.getEventId())
            .eventName(eventName)
            .reasonCategory(r.getReasonCategory())
            .createdAt(r.getCreatedAt())
            .reportResult(r.getReportResult())
            .build();
    }
}