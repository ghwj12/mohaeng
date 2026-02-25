package org.poolpool.mohaeng.event.list.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.domain.PageImpl;
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

        // 지역 범위 계산 (기존 로직 그대로)
        Long regionMin = null;
        Long regionMax = null;

        if (regionId != null) {
            String idStr = String.valueOf(regionId);
            String prefix = idStr.replaceAll("0+$", "");
            if (prefix.length() < 2) {
                prefix = idStr.substring(0, 2);
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

        // ✅ topicIds가 없으면(초기값 = 전체) → 기존 쿼리 그대로 실행
        if (topicIds == null || topicIds.isEmpty()) {
            Page<EventEntity> eventPage = eventRepository.searchEvents(
                    keyword, regionId, regionMin, regionMax,
                    filterStart, filterEnd, categoryId,
                    checkFree, hideClosed, LocalDate.now(),
                    null, pageable
            );
            return eventPage.map(EventDto::fromEntity);
        }

        // ✅ topicIds가 있으면 → 각 topic을 개별적으로 조회 후 OR 합집합 처리
        // LinkedHashMap으로 eventId 기준 중복 제거하면서 순서 유지
        Map<Long, EventEntity> mergedMap = new LinkedHashMap<>();

        for (String topicId : topicIds) {
            String trimmed = topicId.trim();
            if (trimmed.isEmpty()) continue;

            // 각 topicId 하나씩 LIKE '%,1,%' 형태로 검색 (전체 페이지 크기로 조회)
            // Pageable.unpaged() 대신 큰 사이즈로 조회 후 합산
            Page<EventEntity> page = eventRepository.searchEvents(
                    keyword, regionId, regionMin, regionMax,
                    filterStart, filterEnd, categoryId,
                    checkFree, hideClosed, LocalDate.now(),
                    trimmed, Pageable.unpaged()
            );

            for (EventEntity e : page.getContent()) {
                mergedMap.put(e.getEventId(), e);
            }
        }

        // 합집합 결과를 pageable 기준으로 수동 페이징
        List<EventEntity> allMatched = new ArrayList<>(mergedMap.values());
        int total = allMatched.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        List<EventEntity> pageContent = (start >= total)
                ? new ArrayList<>()
                : allMatched.subList(start, end);

        Page<EventEntity> resultPage = new PageImpl<>(pageContent, pageable, total);
        return resultPage.map(EventDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRegionCountDto> getEventCountsByRegion() {
        return eventRepository.countEventsByRegion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDailyCountDto> getDailyEventCountsByRegion(Long regionId) {
        String idStr = String.valueOf(regionId);
        String prefix = idStr.replaceAll("0+$", "");
        if (prefix.length() < 2) prefix = idStr.substring(0, 2);
        StringBuilder minSb = new StringBuilder(prefix);
        StringBuilder maxSb = new StringBuilder(prefix);
        while (minSb.length() < 10) { minSb.append("0"); maxSb.append("9"); }
        Long regionMin = Long.parseLong(minSb.toString());
        Long regionMax = Long.parseLong(maxSb.toString());
        return eventRepository.countDailyEventsByRegion(regionMin, regionMax);
    }
}