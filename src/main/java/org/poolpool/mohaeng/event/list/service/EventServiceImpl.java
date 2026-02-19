package org.poolpool.mohaeng.event.list.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
        // 1. 행사 정보 조회 (이때 영속성 컨텍스트가 파일 리스트를 가지고 있음)
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 행사입니다."));

        // 조회수 증가 로직 (필요시 추가)
        // event.incrementViews(); 

        // 2. 부스 & 부대시설 리스트 가져오기
        List<HostBoothEntity> booths = hostBoothRepository.findByEventId(eventId);
        List<HostFacilityEntity> facilities = hostFacilityRepository.findByEventId(eventId);

        // 3. EventDetailDto로 조립 (EventDto 내부에서 다중 파일 로직 처리됨)
        return EventDetailDto.builder()
                .eventInfo(EventDto.fromEntity(event))
                .booths(booths.stream().map(HostBoothDto::fromEntity).toList())
                .facilities(facilities.stream().map(HostFacilityDto::fromEntity).toList())
                .build();
    }

    // =========================================================================
    // 기존 검색 로직 (그대로 유지)
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> searchEvents(
            Long regionId, LocalDate filterStart, LocalDate filterEnd, 
            Integer categoryId, List<String> topicIds, 
            boolean checkFree, boolean hideClosed, Pageable pageable) {

        Page<EventEntity> eventPage = eventRepository.searchEvents(
                regionId, filterStart, filterEnd, categoryId, checkFree, hideClosed, 
                LocalDate.now(), pageable
        );

        return eventPage.map(entity -> {
            if (!isMatched(entity.getTopicIds(), topicIds)) return null;
            return EventDto.fromEntity(entity); 
        });
    }

    private boolean isMatched(String entityTopics, List<String> selectedTopics) {
        if (selectedTopics == null || selectedTopics.isEmpty()) return true;
        if (entityTopics == null) return false;
        return selectedTopics.stream().anyMatch(Arrays.asList(entityTopics.split(","))::contains);
    }
    
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
