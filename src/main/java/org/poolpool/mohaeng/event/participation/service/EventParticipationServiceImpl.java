package org.poolpool.mohaeng.event.participation.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.poolpool.mohaeng.common.config.UploadProperties;
import org.poolpool.mohaeng.common.util.FileNameChange;
import org.poolpool.mohaeng.event.host.repository.FileRepository;
import org.poolpool.mohaeng.event.list.dto.EventDetailDto;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.entity.FileEntity;
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.poolpool.mohaeng.event.list.service.EventService;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothFacilityDto;
import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothFacilityEntity;
import org.poolpool.mohaeng.event.participation.repository.EventParticipationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {


    private final EventParticipationRepository repo;
    private final FileRepository fileRepository; // 💡 공통 파일 리포지토리 주입
    private final UploadProperties uploadProperties;
    private final EventRepository eventRepository;
    private final EventService eventService;
    
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }
        throw new IllegalStateException("인증 정보를 찾을 수 없습니다.");
    }
    
    // =========================
    // 행사 참여(신청)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<EventParticipationDto> getParticipationList(Long userId) {
        return repo.findParticipationByUserId(userId)
                .stream()
                .map(EventParticipationDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Long saveParticipationTemp(EventParticipationDto dto) {
        EventParticipationEntity e = dto.toEntity();
        e.setPctStatus("임시저장");
        EventParticipationEntity saved = repo.saveParticipation(e);
        return saved.getPctId();
    }

    @Override
    @Transactional
    public Long submitParticipation(EventParticipationDto dto) {
        // 요청 3번: 이름, 연락처, 이메일 등은 시큐리티 컨텍스트 유저 ID로 세팅
        Long userId = getCurrentUserId();
        dto.setUserId(userId);
        
        EventParticipationEntity e = dto.toEntity();
        e.setPctStatus("참여확정");
        EventParticipationEntity saved = repo.saveParticipation(e);
        return saved.getPctId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public EventDetailDto getEventDetail(Long eventId) {
        // 기존 EventController에서 사용하던 상세조회 로직 호출 (조회수는 올리지 않음)
        return eventService.getEventDetail(eventId, false);
    }

    @Override
    @Transactional
    public void cancelParticipation(Long pctId) {
        EventParticipationEntity e = repo.findParticipationById(pctId)
                .orElseThrow(() -> new IllegalArgumentException("참여 신청 없음"));
        e.setPctStatus("취소");
        repo.saveParticipation(e);
    }

    // =========================
    // 부스 참여 신청
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationBoothDto> getParticipationBoothList(Long userId) {
        return repo.findBoothByUserId(userId)
                .stream()
                .map(ParticipationBoothDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Long saveBoothApplyTemp(Long eventId, ParticipationBoothDto dto, List<MultipartFile> files) { // 💡 파라미터 추가!
        
        // 1. 이벤트 검증
        validateEventId(eventId, dto.getHostBoothId());

        // 2. 부스 임시저장 정보 세팅
        ParticipationBoothEntity booth = dto.toEntity();
        booth.setStatus("임시저장");
        ParticipationBoothEntity savedBooth = repo.saveBooth(booth);

        // 3. 부대시설 저장
        saveFacilities(savedBooth.getPctBoothId(), dto.getFacilities());
        
        // 4. 첨부파일 저장 (방금 만든 로직)
        saveFiles(savedBooth, files, eventId);

        return savedBooth.getPctBoothId();
    }
    
    private void validateEventId(Long eventId, Long hostBoothId) {
        Long realEventId = repo.findEventIdByHostBoothId(hostBoothId)
                .orElseThrow(() -> new IllegalArgumentException("HOST_BOOTH 없음"));
        
        if (!realEventId.equals(eventId)) {
            throw new IllegalArgumentException("hostBoothId가 eventId에 속하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void cancelBoothParticipation(Long pctBoothId) {
        ParticipationBoothEntity booth = repo.findBoothById(pctBoothId)
                .orElseThrow(() -> new IllegalArgumentException("부스 신청 없음"));
        booth.setStatus("취소");
        booth.setUpdatedAt(LocalDateTime.now());
        repo.saveBooth(booth);
    }
    
    @Override
    @Transactional
    public Long submitBoothApply(Long eventId, ParticipationBoothDto dto, List<MultipartFile> files) {
        validateEventId(eventId, dto.getHostBoothId());

        ParticipationBoothEntity booth = dto.toEntity();
        booth.setStatus("신청");
        Long userId = getCurrentUserId();
        booth.setUserId(userId);
        ParticipationBoothEntity savedBooth = repo.saveBooth(booth);

        saveFacilities(savedBooth.getPctBoothId(), dto.getFacilities());
        saveFiles(savedBooth, files, eventId);

        // 부스 잔여수량 차감
        repo.decreaseBoothRemainCount(dto.getHostBoothId());

        // ✅ 부대시설 잔여수량 차감
        if (dto.getFacilities() != null) {
            for (ParticipationBoothFacilityDto faci : dto.getFacilities()) {
                repo.decreaseFacilityRemainCount(faci.getHostBoothFaciId(), faci.getFaciCount());
            }
        }

        return savedBooth.getPctBoothId();
    }
    
    private void saveFiles(ParticipationBoothEntity pctBooth, List<MultipartFile> files, Long eventId) {
        if (files == null || files.isEmpty()) return;

        Path pboothDir = uploadProperties.pboothDir(); // C:/upload_files/pbooth

        try {
            if (!Files.exists(pboothDir)) {
                Files.createDirectories(pboothDir);
            }

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                
                // 유틸리티를 사용해 파일명 안전하게 변경
                String renameName = FileNameChange.change(originalName, FileNameChange.RenameStrategy.DATETIME_UUID);
                Path filePath = pboothDir.resolve(renameName);

                EventEntity event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new IllegalArgumentException("행사 없음"));
                
                // 1. 물리적 파일 저장
                file.transferTo(filePath.toFile());

                // 2. 공통 FileEntity를 활용해 DB에 기록
                FileEntity fileEntity = FileEntity.builder()
                        .pctBooth(pctBooth)
                        .event(event) // 부스 참여 엔티티와 연관관계 맺기
                        .fileType("P_BOOTH")     // 파일 타입 구분
                        .originalFileName(originalName)
                        .renameFileName(renameName)
                        .sortOrder(i + 1)        // 파일 순서
                        .createdAt(LocalDateTime.now())
                        .build();

                // 기존 FileRepository를 통해 저장
                fileRepository.save(fileEntity); 
            }
        } catch (IOException e) {
            throw new RuntimeException("참여 부스 첨부파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private void saveFacilities(Long pctBoothId, List<ParticipationBoothFacilityDto> facilities) {
        repo.deleteFacilitiesByPctBoothId(pctBoothId);

        if (facilities == null || facilities.isEmpty()) return;

        List<ParticipationBoothFacilityEntity> entities = facilities.stream()
                .map(f -> f.toEntity(pctBoothId))
                .toList();

        repo.saveFacilities(entities);
    }
}

