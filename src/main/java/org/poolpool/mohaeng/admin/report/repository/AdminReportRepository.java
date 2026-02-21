package org.poolpool.mohaeng.admin.report.repository;

import org.poolpool.mohaeng.admin.report.entity.AdminReportFEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminReportRepository extends JpaRepository<AdminReportFEntity, Long> {

    Page<AdminReportFEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AdminReportFEntity> findByReportResultOrderByCreatedAtDesc(String reportResult, Pageable pageable);

    boolean existsByReporterIdAndEventId(Long reporterId, Long eventId);

    //  승인 시 같은 이벤트의 "다른 모든 신고" 삭제 (PENDING/REJECTED 포함)
    long deleteByEventIdAndReportIdNot(Long eventId, Long reportId);
}