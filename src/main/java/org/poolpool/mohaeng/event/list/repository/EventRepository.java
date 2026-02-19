package org.poolpool.mohaeng.event.list.repository;

import java.time.LocalDate;
import java.util.List;

import org.poolpool.mohaeng.event.list.dto.EventDailyCountDto;
import org.poolpool.mohaeng.event.list.dto.EventRegionCountDto;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

	@Query("SELECT e FROM EventEntity e WHERE "
			+ "(:regionId IS NULL OR e.region.regionId = :regionId OR e.region.parent.regionId = :regionId) AND "
			+ "(:filterStart IS NULL OR e.endDate >= :filterStart) AND "
			+ "(:filterEnd IS NULL OR e.startDate <= :filterEnd) AND "
			+ "(:categoryId IS NULL OR e.category.categoryId = :categoryId) AND "
			+ "(:checkFree = false OR e.price = 0) AND " + "(:hideClosed = false OR e.endDate >= :today)")
	Page<EventEntity> searchEvents( // 설계도 명칭 준수
			@Param("regionId") Long regionId, @Param("filterStart") LocalDate filterStart,
			@Param("filterEnd") LocalDate filterEnd, @Param("categoryId") Integer categoryId,
			@Param("checkFree") boolean checkFree, @Param("hideClosed") boolean hideClosed,
			@Param("today") LocalDate today, Pageable pageable);

	@Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventRegionCountDto(e.region.regionId, COUNT(e)) "
			+ "FROM EventEntity e " + "WHERE e.eventStatus NOT IN ('DELETED', '종료') " + // 삭제되거나 종료된 건 지도에서 제외
			"GROUP BY e.region.regionId")
	List<EventRegionCountDto> countEventsByRegion();

	// 특정 지역(regionId)에서 특정 달(yearMonth)의 날짜별 개수 집계
	// 여기서는 이해하기 쉽게 '시작일' 기준으로 해당 월의 개수를 세는 예시입니다.
	@Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventDailyCountDto(e.startDate, COUNT(e)) "
			+ "FROM EventEntity e " + "WHERE e.region.regionId = :regionId " + "AND e.eventStatus NOT IN ('DELETED') "
			+ "GROUP BY e.startDate")
	List<EventDailyCountDto> countDailyEventsByRegion(@Param("regionId") Long regionId);
}