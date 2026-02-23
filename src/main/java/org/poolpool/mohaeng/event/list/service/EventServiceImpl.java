package org.poolpool.mohaeng.event.list.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.poolpool.mohaeng.event.list.entity.FileEntity;
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
        // 기존 코드와 완전히 동일합니다.
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 행사입니다."));

        event.setViews(event.getViews() + 1);
        EventDto eventDto = EventDto.fromEntity(event);

        List<String> detailImages = new ArrayList<>();
        List<String> boothImages = new ArrayList<>();

        if (event.getEventFiles() != null) {
            for (FileEntity file : event.getEventFiles()) {
                if ("EVENT".equals(file.getFileType())) {
                    detailImages.add(file.getRenameFileName());
                } else if ("HBOOTH".equals(file.getFileType())) {
                    boothImages.add(file.getRenameFileName());
                }
            }
        }

        eventDto.setDetailImagePaths(detailImages);
        eventDto.setBoothFilePaths(boothImages);

        List<HostBoothEntity> booths = hostBoothRepository.findByEventId(eventId);
        List<HostFacilityEntity> facilities = hostFacilityRepository.findByEventId(eventId);

        return EventDetailDto.builder()
                .eventInfo(eventDto)
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

        String topicParam = (topicIds == null || topicIds.isEmpty()) ? null : String.join(",", topicIds);

        Long regionMin = null;
        Long regionMax = null;
        
        if (regionId != null) {
            String idStr = String.valueOf(regionId);
            
            // 💡 핵심 버그 수정: 뒤에 붙은 0을 지우되, 최소 2자리(시/도)는 무조건 남깁니다!
            String prefix = idStr.replaceAll("0+$", "");
            if (prefix.length() < 2) {
                prefix = idStr.substring(0, 2); // "5"가 되면 "50"(제주)으로 복구
            }
            
            StringBuilder minSb = new StringBuilder(prefix);
            StringBuilder maxSb = new StringBuilder(prefix);
            
            while (minSb.length() < 10) {
                minSb.append("0");
                maxSb.append("9");
            }
            regionMin = Long.parseLong(minSb.toString());
            regionMax = Long.parseLong(maxSb.toString());
        }

        // Repository 쿼리 실행
        Page<EventEntity> eventPage = eventRepository.searchEvents(
                keyword, regionId, regionMin, regionMax, filterStart, filterEnd, categoryId, checkFree, hideClosed, 
                LocalDate.now(), topicParam, pageable
        );

        return eventPage.map(EventDto::fromEntity);
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