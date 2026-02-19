package org.poolpool.mohaeng.event.host.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.entity.HostBoothEntity;
import org.poolpool.mohaeng.event.host.entity.HostFacilityEntity;
import org.poolpool.mohaeng.event.host.repository.HostBoothRepository;
import org.poolpool.mohaeng.event.host.repository.HostFacilityRepository;
import org.poolpool.mohaeng.event.host.repository.FileRepository; // 👈 추가
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.entity.FileEntity; // 👈 추가
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
    private final FileRepository fileRepository; // 👈 추가

    @Override
    @Transactional
    public Long createEventWithDetails(EventCreateDto createDto) {
        
        // 1. 행사(Event) 저장 (여기서 thumbnail 필드는 자동으로 들어감)
        EventDto eventDto = createDto.getEventInfo();
        EventEntity eventEntity = eventDto.toEntity();
        EventEntity savedEvent = eventRepository.save(eventEntity);
        Long eventId = savedEvent.getEventId();

        // 2. 상세 사진들 저장 (DETAIL)
        if (eventDto.getDetailImagePaths() != null && !eventDto.getDetailImagePaths().isEmpty()) {
            for (String path : eventDto.getDetailImagePaths()) {
                FileEntity detailFile = FileEntity.builder()
                        .event(savedEvent)
                        .fileType("DETAIL")
                        .originalFileName(path) // 원본명도 일단 경로로 저장 (나중에 필요시 수정)
                        .renameFileName(path)
                        .createdAt(LocalDateTime.now())
                        .build();
                fileRepository.save(detailFile);
            }
        }

        // 3. 부스 관련 파일들 저장 (BOOTH)
        if (eventDto.getBoothFilePaths() != null && !eventDto.getBoothFilePaths().isEmpty()) {
            for (String path : eventDto.getBoothFilePaths()) {
                FileEntity boothFile = FileEntity.builder()
                        .event(savedEvent)
                        .fileType("BOOTH")
                        .originalFileName(path)
                        .renameFileName(path)
                        .createdAt(LocalDateTime.now())
                        .build();
                fileRepository.save(boothFile);
            }
        }

        // 4. 부스(Booth) 리스트 저장
        if (createDto.getBooths() != null) {
            List<HostBoothEntity> boothEntities = createDto.getBooths().stream()
                    .map(dto -> {
                        dto.setEventId(eventId);
                        return dto.toEntity();
                    })
                    .collect(Collectors.toList());
            hostBoothRepository.saveAll(boothEntities);
        }

        // 5. 부대시설(Facility) 리스트 저장
        if (createDto.getFacilities() != null) {
            List<HostFacilityEntity> facilityEntities = createDto.getFacilities().stream()
                    .map(dto -> {
                        dto.setEventId(eventId);
                        return dto.toEntity();
                    })
                    .collect(Collectors.toList());
            hostFacilityRepository.saveAll(facilityEntities);
        }

        return eventId;
    }
    
    @Override
    @Transactional // DB 수정을 위해 트랜잭션 필수!
    public void deleteEvent(Long eventId) {
        // 1. 해당 행사 조회
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 행사를 찾을 수 없습니다."));

        // 2. 상태를 DELETED로 변경
        // (EventEntity에 changeStatusToDeleted() 메서드가 있어야 합니다!)
        event.changeStatusToDeleted();
    }
}