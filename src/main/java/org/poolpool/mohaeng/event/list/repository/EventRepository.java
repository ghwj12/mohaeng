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
            + "(:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') OR e.simpleExplain LIKE CONCAT('%', :keyword, '%')) AND "
            + "(:regionId IS NULL OR e.region.regionId BETWEEN :regionMin AND :regionMax) AND "
            + "(:filterStart IS NULL OR e.endDate >= :filterStart) AND "
            + "(:filterEnd IS NULL OR e.startDate <= :filterEnd) AND "
            + "(:categoryId IS NULL OR e.category.categoryId = :categoryId) AND "
            + "(:checkFree = false OR e.price = 0) AND "
            + "(:hideClosed = false OR e.endDate >= :today) AND "
            + "(:topicIds IS NULL OR e.topicIds LIKE CONCAT('%', :topicIds, '%'))")
    Page<EventEntity> searchEvents(
            @Param("keyword") String keyword,
            @Param("regionId") Long regionId,
            @Param("regionMin") Long regionMin,
            @Param("regionMax") Long regionMax,
            @Param("filterStart") LocalDate filterStart,
            @Param("filterEnd") LocalDate filterEnd,
            @Param("categoryId") Integer categoryId,
            @Param("checkFree") boolean checkFree,
            @Param("hideClosed") boolean hideClosed,
            @Param("today") LocalDate today,
            @Param("topicIds") String topicIds,
            Pageable pageable);
    @Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventRegionCountDto(e.region.regionId, COUNT(e)) "
            + "FROM EventEntity e " + "WHERE e.eventStatus NOT IN ('DELETED', '종료') " +
            "GROUP BY e.region.regionId")
    List<EventRegionCountDto> countEventsByRegion();
    @Query("SELECT new org.poolpool.mohaeng.event.list.dto.EventDailyCountDto(e.startDate, COUNT(e)) "
            + "FROM EventEntity e " + "WHERE e.region.regionId = :regionId " + "AND e.eventStatus NOT IN ('DELETED') "
            + "GROUP BY e.startDate")
    List<EventDailyCountDto> countDailyEventsByRegion(@Param("regionId") Long regionId);
}