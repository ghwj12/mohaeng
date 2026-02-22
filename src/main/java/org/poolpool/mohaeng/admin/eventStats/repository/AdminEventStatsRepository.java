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

    // 1. 전체 행사 분석 (필터링 적용)
    // QueryDSL 대신 @Query 내부에서 IS NULL 과 OR 조건을 활용해 동적 쿼리를 구현합니다.
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

    // 2. 월별 행사 수 조회 (선택한 연도 기준)
    // MONTH()와 YEAR() 함수를 사용하여 집계합니다.
    @Query("SELECT new org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto$MonthlyStatsResponse(" +
           "MONTH(e.startDate), COUNT(e)) " +
           "FROM EventEntity e " +
           "WHERE YEAR(e.startDate) = :year " +
           "GROUP BY MONTH(e.startDate) " +
           "ORDER BY MONTH(e.startDate) ASC")
    List<AdminEventStatsDto.MonthlyStatsResponse> countByMonth(@Param("year") int year);

    // 3. 카테고리 행사 수 조회 (진행중인 행사)
    @Query("SELECT new org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto$CategoryStatsResponse(" +
           "c.categoryName, COUNT(e)) " +
           "FROM EventEntity e LEFT JOIN e.category c " +
           "WHERE e.eventStatus = 'ONGOING' " +
           "GROUP BY c.categoryName")
    List<AdminEventStatsDto.CategoryStatsResponse> countByCategory();
    
    // --- [단일 행사 분석용 쿼리 추가] ---

    // 1. 특정 행사의 결제 완료된 참여자 수 조회
    @Query("SELECT COUNT(p) FROM EventParticipationEntity p WHERE p.eventId = :eventId AND p.pctStatus = '참여확정(결제완료)'")
    long countParticipantsByEventId(@Param("eventId") Long eventId);

    // 2. 특정 행사의 리뷰 수 조회
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.event.eventId = :eventId")
    long countReviewsByEventId(@Param("eventId") Long eventId);

    // 3. 특정 행사의 관심(찜) 수 조회
    @Query("SELECT COUNT(w) FROM EventWishlistEntity w WHERE w.eventId = :eventId")
    long countWishlistByEventId(@Param("eventId") Long eventId);

    // 4. 참여자 성별 통계 (남/여 카운트)
    @Query("SELECT p.pctGender, COUNT(p) FROM EventParticipationEntity p WHERE p.eventId = :eventId AND p.pctStatus = '참여확정(결제완료)' GROUP BY p.pctGender")
    List<Object[]> countGenderByEventId(@Param("eventId") Long eventId);

    // 5. 🛠️ 부스 수익 합산 (에러 수정 완료: hb.boothId 사용)
    @Query("SELECT SUM(pb.totalPrice) FROM ParticipationBoothEntity pb WHERE pb.status = '결제완료' AND pb.hostBoothId IN (SELECT hb.boothId FROM HostBoothEntity hb WHERE hb.eventId = :eventId)")
    Long sumBoothRevenueByEventId(@Param("eventId") Long eventId);
}