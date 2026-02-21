package org.poolpool.mohaeng.admin.report.service;

import java.util.List;

import org.poolpool.mohaeng.admin.report.dto.AdminReportCreateRequestDto;
import org.poolpool.mohaeng.admin.report.dto.AdminReportDetailDto;
import org.poolpool.mohaeng.admin.report.dto.AdminReportListItemDto;
import org.poolpool.mohaeng.admin.report.entity.AdminReportFEntity;
import org.poolpool.mohaeng.admin.report.repository.AdminReportRepository;
import org.poolpool.mohaeng.admin.report.type.ReportResult;
import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final AdminReportRepository reportRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminReportListItemDto> getList(Pageable pageable) {
        Page<AdminReportFEntity> page = reportRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<AdminReportListItemDto> items = page.getContent().stream()
            .map(r -> AdminReportListItemDto.fromEntity(r, getEventNameSafe(r.getEventId())))
            .toList();

        return new PageResponse<>(items, pageable.getPageNumber(), pageable.getPageSize(),
            page.getTotalElements(), page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminReportDetailDto getDetail(long reportId) {
        AdminReportFEntity r = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        return AdminReportDetailDto.fromEntity(r, getEventNameSafe(r.getEventId()));
    }

    @Override
    @Transactional
    public long create(long reporterId, AdminReportCreateRequestDto request) {
        Long eventId = request.getEventId();

        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("존재하지 않는 이벤트입니다.");
        }

        if (reportRepository.existsByReporterIdAndEventId(reporterId, eventId)) {
            throw new IllegalStateException("이미 해당 이벤트를 신고했습니다.");
        }

        AdminReportFEntity r = AdminReportFEntity.builder()
            .eventId(eventId)
            .reporterId(reporterId)
            .reasonCategory(request.getReasonCategory())
            .reasonDetailText(request.getReasonDetailText())
            .reportResult(ReportResult.PENDING)
            .build();

        return reportRepository.save(r).getReportId();
    }

    @Override
    @Transactional
    public void approve(long reportId) {
        AdminReportFEntity r = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        if (!ReportResult.PENDING.equals(r.getReportResult())) {
            throw new IllegalStateException("이미 처리된 신고입니다.");
        }

        // 1) 승인
        r.setReportResult(ReportResult.APPROVED);

        // 2) 이벤트 비활성화(DELETED)
        EventEntity event = eventRepository.findById(r.getEventId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이벤트입니다."));
        event.changeStatusToDeleted();

        // 3)  같은 이벤트의 다른 "모든 신고" 삭제(PENDING/REJECTED 포함)
        reportRepository.deleteByEventIdAndReportIdNot(r.getEventId(), r.getReportId());
    }

    @Override
    @Transactional
    public void reject(long reportId) {
        AdminReportFEntity r = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        if (!ReportResult.PENDING.equals(r.getReportResult())) {
            throw new IllegalStateException("이미 처리된 신고입니다.");
        }

        r.setReportResult(ReportResult.REJECTED);
    }

    private String getEventNameSafe(Long eventId) {
        return eventRepository.findById(eventId)
            .map(EventEntity::getTitle)
            .orElse("(삭제/미존재 이벤트)");
    }
}