package org.poolpool.mohaeng.admin.eventStats.repository;

import org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdminEventStatsRepository extends JpaRepository<EventEntity, Long> {

    // 1. 전체 행사 분석 (기존 유지)
    @Query("SELECT new org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto$EventListResponse(" +
           "e.eventId, e.title, c.categoryName, e.lotNumberAdr, e.eventStatus, e.startDate, e.endDate, e.views) " +
           "FROM EventEntity e LEFT JOIN e.category c " +
           "WHERE (:startDate IS NULL OR e.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.endDate <= :endDate) " +
           "AND (:category IS NULL OR :category = '' OR c.categoryName = :category) " +
           "AND (:location IS NULL OR :location = '' OR e.lotNumberAdr LIKE CONCAT('%', :location, '%')) " +
           "AND (:status IS NULL OR :status = '' OR e.eventStatus = :status) " +
           "ORDER BY e.eventId DESC")
    List<AdminEventStatsDto.EventListResponse> findAllEventsFiltered(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") String category,
            @Param("location") String location,
            @Param("status") String status
    );

    // 2. 월별 행사 수 조회 (기존 유지)
    @Query("SELECT new org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto$MonthlyStatsResponse(" +
           "MONTH(e.startDate), COUNT(e)) " +
           "FROM EventEntity e " +
           "WHERE YEAR(e.startDate) = :year " +
           "GROUP BY MONTH(e.startDate) " +
           "ORDER BY MONTH(e.startDate) ASC")
    List<AdminEventStatsDto.MonthlyStatsResponse> countByMonth(@Param("year") int year);

    // 3. 카테고리 행사 수 조회
    // 💡 수정: 'ONGOING'을 실제 DB 값인 '행사중'으로 변경
    @Query("SELECT new org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto$CategoryStatsResponse(" +
           "c.categoryName, COUNT(e)) " +
           "FROM EventEntity e LEFT JOIN e.category c " +
           "WHERE e.eventStatus = '행사중' " +
           "GROUP BY c.categoryName")
    List<AdminEventStatsDto.CategoryStatsResponse> countByCategory();
    
    // --- [단일 행사 분석용 쿼리] ---

    // 1. 특정 행사의 참여자 수 조회
    // 💡 수정: 서비스에서 사용하는 '결제대기' 또는 실제 완료 상태인 '결제완료'로 매칭
    // (보통 통계는 결제가 완료된 사람만 세는 것이 좋으므로 '결제완료'를 추천합니다.)
    @Query("SELECT COUNT(p) FROM EventParticipationEntity p WHERE p.eventId = :eventId AND p.pctStatus = '결제완료'")
    long countParticipantsByEventId(@Param("eventId") Long eventId);

    // 2. 리뷰 수 조회 (기존 유지)
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.event.eventId = :eventId")
    long countReviewsByEventId(@Param("eventId") Long eventId);

    // 3. 관심(찜) 수 조회 (기존 유지)
    @Query("SELECT COUNT(w) FROM EventWishlistEntity w WHERE w.eventId = :eventId")
    long countWishlistByEventId(@Param("eventId") Long eventId);

    // 4. 참여자 성별 통계
    // 💡 수정: pctStatus 조건을 '결제완료'로 통일
    @Query("SELECT p.pctGender, COUNT(p) FROM EventParticipationEntity p WHERE p.eventId = :eventId AND p.pctStatus = '결제완료' GROUP BY p.pctGender")
    List<Object[]> countGenderByEventId(@Param("eventId") Long eventId);

    // 5. 부스 수익 합산
    // 💡 수정: 서비스에서 부스 신청 시 '신청'으로 들어가므로, 결제 로직이 따로 있다면 '결제완료'를 유지하고,
    // 아니면 '신청' 상태를 합산하도록 변경해야 합니다. 여기서는 통계이므로 '결제완료'를 유지합니다.
    @Query("SELECT SUM(pb.totalPrice) FROM ParticipationBoothEntity pb WHERE pb.status = '결제완료' AND pb.hostBoothId IN (SELECT hb.boothId FROM HostBoothEntity hb WHERE hb.eventId = :eventId)")
    Long sumBoothRevenueByEventId(@Param("eventId") Long eventId);
}