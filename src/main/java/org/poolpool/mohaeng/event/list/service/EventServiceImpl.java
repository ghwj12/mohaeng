package org.poolpool.mohaeng.event.list.service;

import java.time.LocalDate;
import java.util.List; // Arrays는 삭제됨

import org.poolpool.mohaeng.event.host.dto.HostBoothDto;
import org.poolpool.mohaeng.event.host.dto.HostFacilityDto;
import org.poolpool.mohaeng.event.host.entity.HostBoothEntity;
import org.poolpool.mohaeng.event.host.entity.HostFacilityEntity;
import org.poolpool.mohaeng.event.host.repository.HostBoothRepository;
import org.poolpool.mohaeng.event.host.repository.HostFacilityRepository;
import org.poolpool.mohaeng.event.list.dto.EventDailyCountDto;
import org.poolpool.mohaeng.event.list.dto.EventDetailDto;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.dto.EventRegionCountDto;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final HostBoothRepository hostBoothRepository;
    private final HostFacilityRepository hostFacilityRepository;

    @Override
    @Transactional
    public EventDetailDto getEventDetail(Long eventId) {
        // 1. 행사 조회
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 행사입니다."));

        // 2. 조회수 1 증가 (자동 업데이트)
        event.setViews(event.getViews() + 1);

        // 3. 부스 및 부대시설 조회
        List<HostBoothEntity> booths = hostBoothRepository.findByEventId(eventId);
        List<HostFacilityEntity> facilities = hostFacilityRepository.findByEventId(eventId);

        // 4. DTO 조립 및 반환
        return EventDetailDto.builder()
                .eventInfo(EventDto.fromEntity(event))
                // 주최자(User) 정보 매핑
                .hostName(event.getHost() != null ? event.getHost().getName() : "정보 없음")
                .hostEmail(event.getHost() != null ? event.getHost().getEmail() : "정보 없음")
                .hostPhone(event.getHost() != null ? event.getHost().getPhone() : "정보 없음")
                .booths(booths.stream().map(HostBoothDto::fromEntity).toList())
                .facilities(facilities.stream().map(HostFacilityDto::fromEntity).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> searchEvents(
    		String keyword, Long regionId, LocalDate filterStart, LocalDate filterEnd, 
            Integer categoryId, List<String> topicIds, 
            boolean checkFree, boolean hideClosed, Pageable pageable) {

        // DB 쿼리용 문자열 변환
        String topicParam = (topicIds == null || topicIds.isEmpty()) ? null : String.join(",", topicIds);

        Page<EventEntity> eventPage = eventRepository.searchEvents(
        		keyword, regionId, filterStart, filterEnd, categoryId, checkFree, hideClosed, 
                LocalDate.now(), topicParam, pageable
        );

        return eventPage.map(EventDto::fromEntity);
    }

    // 🗑️ isMatched 메서드는 DB 필터링으로 대체되었으므로 삭제했습니다.
    
    @Override
    @Transactional(readOnly = true)
    public List<EventRegionCountDto> getEventCountsByRegion() {
        return eventRepository.countEventsByRegion();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EventDailyCountDto> getDailyEventCountsByRegion(Long regionId) {
        return eventRepository.countDailyEventsByRegion(regionId);
    }
}
