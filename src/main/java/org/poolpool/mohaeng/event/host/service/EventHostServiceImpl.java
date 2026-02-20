package org.poolpool.mohaeng.event.host.service;

import java.util.List;
import java.util.stream.Collectors;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.entity.HostBoothEntity;
import org.poolpool.mohaeng.event.host.entity.HostFacilityEntity;
import org.poolpool.mohaeng.event.host.repository.HostBoothRepository;
import org.poolpool.mohaeng.event.host.repository.HostFacilityRepository;
import org.poolpool.mohaeng.event.host.repository.FileRepository;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.entity.EventCategoryEntity;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.entity.EventRegionEntity;
import org.poolpool.mohaeng.event.list.entity.FileEntity;
import org.poolpool.mohaeng.event.list.repository.EventCategoryRepository; // 추가 필요
import org.poolpool.mohaeng.event.list.repository.EventRegionRepository;   // 추가 필요
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventHostServiceImpl implements EventHostService {

    private final EventRepository eventRepository;
    private final HostBoothRepository hostBoothRepository;
    private final HostFacilityRepository hostFacilityRepository;
    private final FileRepository fileRepository;
    
    // DB에서 기존 데이터를 조회하기 위해 추가
    private final EventCategoryRepository eventCategoryRepository;
    private final EventRegionRepository eventRegionRepository;

    @Override
    @Transactional
    public Long createEventWithDetails(EventCreateDto createDto) {
        
        // 1. DTO로부터 엔티티 생성
        EventDto eventDto = createDto.getEventInfo();
        EventEntity eventEntity = eventDto.toEntity();

        // 2. [에러 방지] DB에 실제 존재하는 카테고리와 지역 정보 연결
        if (eventDto.getCategory() != null) {
            EventCategoryEntity category = eventCategoryRepository.findById(eventDto.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));
            
            EventRegionEntity region = eventRegionRepository.findById(eventDto.getRegion().getRegionId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다."));
            
            // 엔티티에 진짜 DB 객체 주입
            eventEntity.updateCategoryAndRegion(category, region);
        }

        // 3. 행사(Event) 저장
        EventEntity savedEvent = eventRepository.save(eventEntity);
        Long eventId = savedEvent.getEventId();

        // 4. 상세 페이지 다중 사진 저장 (DETAIL)
        if (eventDto.getDetailImagePaths() != null && !eventDto.getDetailImagePaths().isEmpty()) {
            for (String path : eventDto.getDetailImagePaths()) {
                FileEntity detailFile = FileEntity.builder()
                        .event(savedEvent)
                        .fileType("DETAIL")
                        .originalFileName(path) 
                        .renameFileName(path)
                        .build();
                fileRepository.save(detailFile);
            }
        }

        // 5. 부스 관련 다중 파일 저장 (BOOTH)
        if (eventDto.getBoothFilePaths() != null && !eventDto.getBoothFilePaths().isEmpty()) {
            for (String path : eventDto.getBoothFilePaths()) {
                FileEntity boothFile = FileEntity.builder()
                        .event(savedEvent)
                        .fileType("BOOTH")
                        .originalFileName(path)
                        .renameFileName(path)
                        .build();
                fileRepository.save(boothFile);
            }
        }

        // 6. 부스(Booth) 리스트 저장
        if (createDto.getBooths() != null) {
            List<HostBoothEntity> boothEntities = createDto.getBooths().stream()
                    .map(dto -> {
                        // DB에 방금 저장된 행사 ID 연결
                        return HostBoothEntity.builder()
                                .eventId(eventId)
                                .boothName(dto.getBoothName())
                                .boothPrice(dto.getBoothPrice())
                                .boothSize(dto.getBoothSize())
                                .boothNote(dto.getBoothNote())
                                .totalCount(dto.getTotalCount())
                                .remainCount(dto.getTotalCount()) // 초기 잔여량은 전체량과 동일
                                .build();
                    })
                    .collect(Collectors.toList());
            hostBoothRepository.saveAll(boothEntities);
        }

        // 7. 부대시설(Facility) 리스트 저장
        if (createDto.getFacilities() != null) {
            List<HostFacilityEntity> facilityEntities = createDto.getFacilities().stream()
                    .map(dto -> {
                        return HostFacilityEntity.builder()
                                .eventId(eventId)
                                .faciName(dto.getFaciName())
                                .faciPrice(dto.getFaciPrice())
                                .faciUnit(dto.getFaciUnit())
                                .hasCount(dto.getHasCount())
                                .totalCount(dto.getTotalCount())
                                .remainCount(dto.getTotalCount())
                                .build();
                    })
                    .collect(Collectors.toList());
            hostFacilityRepository.saveAll(facilityEntities);
        }

        return eventId;
    }
    
    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 행사를 찾을 수 없습니다."));

        event.changeStatusToDeleted();
    }
}