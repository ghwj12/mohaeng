package org.poolpool.mohaeng.admin.report.dto;

import java.time.LocalDateTime;

import org.poolpool.mohaeng.admin.report.entity.AdminReportFEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReportDetailDto {
    private Long reportId;
    private Long eventId;
    private String eventName;
    private Long reporterId;
    private String reasonCategory;
    private String reasonDetailText;
    private LocalDateTime createdAt;
    private String reportResult;

    public static AdminReportDetailDto fromEntity(AdminReportFEntity r, String eventName) {
        return AdminReportDetailDto.builder()
            .reportId(r.getReportId())
            .eventId(r.getEventId())
            .eventName(eventName)
            .reporterId(r.getReporterId())
            .reasonCategory(r.getReasonCategory())
            .reasonDetailText(r.getReasonDetailText())
            .createdAt(r.getCreatedAt())
            .reportResult(r.getReportResult())
            .build();
    }
}