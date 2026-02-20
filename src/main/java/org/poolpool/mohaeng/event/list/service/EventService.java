package org.poolpool.mohaeng.event.list.service;

import java.time.LocalDate;
import java.util.List;

import org.poolpool.mohaeng.event.list.dto.EventDailyCountDto;
import org.poolpool.mohaeng.event.list.dto.EventDetailDto;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.dto.EventRegionCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    /**
     * 7가지 다중 필터를 적용하여 이벤트 목록을 조회합니다.
     * 리턴 타입은 설계도의 PageResponse 대신 스프링 기본 Page를 사용하여 DTO 생성을 최소화합니다.
     */
    Page<EventDto> searchEvents(
    		String keyword,
            Long regionId,          // 1&2. 지역 필터
            LocalDate filterStart,  // 3. 시작 기간
            LocalDate filterEnd,    // 3. 종료 기간
            Integer categoryId,     // 4. 카테고리
            List<String> topicIds,  // 5. 주제 (다중 선택)
            boolean checkFree,      // 7. 무료만 보기
            boolean hideClosed,     // 6. 종료 가리기
            Pageable pageable       // 페이징 정보
    );
    
    EventDetailDto getEventDetail(Long eventId);
    List<EventRegionCountDto> getEventCountsByRegion();
    List<EventDailyCountDto> getDailyEventCountsByRegion(Long regionId);
}