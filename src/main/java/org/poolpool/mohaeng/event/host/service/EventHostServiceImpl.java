package org.poolpool.mohaeng.event.host.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.poolpool.mohaeng.common.config.UploadProperties;
import org.poolpool.mohaeng.common.util.FileNameChange;
import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.entity.HostBoothEntity;
import org.poolpool.mohaeng.event.host.entity.HostFacilityEntity;
import org.poolpool.mohaeng.event.host.repository.FileRepository;
import org.poolpool.mohaeng.event.host.repository.HostBoothRepository;
import org.poolpool.mohaeng.event.host.repository.HostFacilityRepository;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.entity.EventCategoryEntity;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.entity.EventRegionEntity;
import org.poolpool.mohaeng.event.list.entity.FileEntity;
import org.poolpool.mohaeng.event.list.repository.EventCategoryRepository;
import org.poolpool.mohaeng.event.list.repository.EventRegionRepository;
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
    // 💡 파일 저장 경로를 가져오기 위한 설정
    private final UploadProperties uploadProperties;

    @Override
    @Transactional
    public Long createEventWithDetails(EventCreateDto createDto, MultipartFile thumbnail, 
                                       List<MultipartFile> detailFiles, List<MultipartFile> boothFiles) {
        
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

        // 💡 3. 썸네일(프로필 사진) 물리 저장 & 엔티티 세팅 (1장)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String original = thumbnail.getOriginalFilename();
            String rename = FileNameChange.change(original, FileNameChange.RenameStrategy.DATETIME_UUID);
            File saveDir = uploadProperties.boardDir().toFile(); // C:/upload_files/event
            
            if (!saveDir.exists()) saveDir.mkdirs(); // 폴더 없으면 생성
            
            try {
                thumbnail.transferTo(new File(saveDir, rename));
                eventEntity.setThumbnail(rename); // DB thumbnail 컬럼에 저장될 이름 세팅
            } catch (Exception e) {
                throw new RuntimeException("썸네일 업로드 실패", e);
            }
        }

        // 4. 행사(Event) 저장 (행사 ID를 얻기 위해 먼저 저장)
        EventEntity savedEvent = eventRepository.save(eventEntity);
        Long eventId = savedEvent.getEventId();

        // 💡 5. 상세 페이지 다중 사진 물리 저장 및 FILE 테이블 기록 (여러 장)
        if (detailFiles != null && !detailFiles.isEmpty()) {
            File saveDir = uploadProperties.boardDir().toFile(); // C:/upload_files/event
            if (!saveDir.exists()) saveDir.mkdirs();
            
            saveMultiFiles(detailFiles, saveDir, savedEvent, "EVENT");
        }

        // 💡 6. 부스 첨부파일 다중 물리 저장 및 FILE 테이블 기록 (여러 장)
        if (boothFiles != null && !boothFiles.isEmpty()) {
            File saveDir = uploadProperties.hboothDir().toFile(); // C:/upload_files/hbooth
            if (!saveDir.exists()) saveDir.mkdirs();
            
            saveMultiFiles(boothFiles, saveDir, savedEvent, "HBOOTH");
        }

        // 7. 부스(Booth) 리스트 저장
        if (createDto.getBooths() != null) {
            List<HostBoothEntity> boothEntities = createDto.getBooths().stream()
                    .map(dto -> {
                        return HostBoothEntity.builder()
                                .eventId(eventId)
                                .boothName(dto.getBoothName())
                                .boothPrice(dto.getBoothPrice())
                                .boothSize(dto.getBoothSize())
                                .boothNote(dto.getBoothNote())
                                .totalCount(dto.getTotalCount())
                                .remainCount(dto.getTotalCount())
                                .build();
                    })
                    .collect(Collectors.toList());
            hostBoothRepository.saveAll(boothEntities);
        }

        // 8. 부대시설(Facility) 리스트 저장
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
    
    // 💡 중복되는 '물리 파일 저장 + DB File 테이블 기록' 로직을 분리한 헬퍼 메서드
    private void saveMultiFiles(List<MultipartFile> files, File saveDir, EventEntity event, String fileType) {
        int sortOrder = 1;
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            
            String original = file.getOriginalFilename();
            String rename = FileNameChange.change(original, FileNameChange.RenameStrategy.DATETIME_UUID);
            
            try {
                file.transferTo(new File(saveDir, rename)); // 실제 C 드라이브 폴더에 파일 저장!
                
                // FileEntity DB에 기록 (Builder에 createdAt 명시)
                FileEntity fileEntity = FileEntity.builder()
                        .event(event)
                        .fileType(fileType)
                        .originalFileName(original)
                        .renameFileName(rename)
                        .sortOrder(sortOrder++) // 파일 순서 (1, 2, 3...)
                        .createdAt(LocalDateTime.now()) 
                        .build();
                fileRepository.save(fileEntity);
                
            } catch (Exception e) {
                throw new RuntimeException(fileType + " 다중 파일 업로드 실패", e);
            }
        }
    }
    
    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 행사를 찾을 수 없습니다."));

        event.changeStatusToDeleted();
    }
}