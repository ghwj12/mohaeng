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
            + "(:checkFree = false OR e.price = 0) AND "
            + "(:hideClosed = false OR e.endDate >= :today) AND "
            + "(:topicIds IS NULL OR e.topicIds LIKE CONCAT('%', :topicIds, '%'))") // SQL 문법에 맞게 수정
    Page<EventEntity> searchEvents(
            @Param("regionId") Long regionId, 
            @Param("filterStart") LocalDate filterStart,
            @Param("filterEnd") LocalDate filterEnd, 
            @Param("categoryId") Integer categoryId,
            @Param("checkFree") boolean checkFree, 
            @Param("hideClosed") boolean hideClosed,
            @Param("today") LocalDate today, 
            @Param("topicIds") String topicIds, // 👈 8번째 인자로 추가!
            Pageable pageable); // 👈 9번째 인자

    @Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventRegionCountDto(e.region.regionId, COUNT(e)) "
            + "FROM EventEntity e " + "WHERE e.eventStatus NOT IN ('DELETED', '종료') " +
            "GROUP BY e.region.regionId")
    List<EventRegionCountDto> countEventsByRegion();

    @Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventDailyCountDto(e.startDate, COUNT(e)) "
            + "FROM EventEntity e " + "WHERE e.region.regionId = :regionId " + "AND e.eventStatus NOT IN ('DELETED') "
            + "GROUP BY e.startDate")
    List<EventDailyCountDto> countDailyEventsByRegion(@Param("regionId") Long regionId);
}