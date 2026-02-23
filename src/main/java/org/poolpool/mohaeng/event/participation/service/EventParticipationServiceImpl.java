package org.poolpool.mohaeng.event.participation.service;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothFacilityDto;
import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothEntity;
import org.poolpool.mohaeng.event.participation.entity.ParticipationBoothFacilityEntity;
import org.poolpool.mohaeng.event.participation.repository.EventParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {


    private final EventParticipationRepository repo;

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
        EventParticipationEntity e = dto.toEntity();
        e.setPctStatus("결제대기");
        EventParticipationEntity saved = repo.saveParticipation(e);
        return saved.getPctId();
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
    public Long saveBoothApplyTemp(Long eventId, ParticipationBoothDto dto) {

        // hostBoothId가 eventId 소속인지 검증 (repo 메서드 필요)
        Long realEventId = repo.findEventIdByHostBoothId(dto.getHostBoothId())
                .orElseThrow(() -> new IllegalArgumentException("HOST_BOOTH 없음"));

        if (!realEventId.equals(eventId)) {
            throw new IllegalArgumentException("hostBoothId가 eventId에 속하지 않습니다.");
        }

        ParticipationBoothEntity booth = dto.toEntity(); // ⚠️ 여기서 eventId set 있으면 제거해야 함(아직 남아있으면 계속 터짐)
        booth.setStatus("임시저장");
        ParticipationBoothEntity savedBooth = repo.saveBooth(booth);

        saveFacilities(savedBooth.getPctBoothId(), dto.getFacilities());
        return savedBooth.getPctBoothId();
    }

    @Override
    @Transactional
    public Long submitBoothApply(Long eventId, ParticipationBoothDto dto) {

        Long realEventId = repo.findEventIdByHostBoothId(dto.getHostBoothId())
                .orElseThrow(() -> new IllegalArgumentException("HOST_BOOTH 없음"));

        if (!realEventId.equals(eventId)) {
            throw new IllegalArgumentException("hostBoothId가 eventId에 속하지 않습니다.");
        }

        ParticipationBoothEntity booth = dto.toEntity();
        booth.setStatus("신청");
        ParticipationBoothEntity savedBooth = repo.saveBooth(booth);

        saveFacilities(savedBooth.getPctBoothId(), dto.getFacilities());
        return savedBooth.getPctBoothId();
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

    private void saveFacilities(Long pctBoothId, List<ParticipationBoothFacilityDto> facilities) {
        repo.deleteFacilitiesByPctBoothId(pctBoothId);

        if (facilities == null || facilities.isEmpty()) return;

        List<ParticipationBoothFacilityEntity> entities = facilities.stream()
                .map(f -> f.toEntity(pctBoothId))
                .toList();

        repo.saveFacilities(entities);
    }
}

