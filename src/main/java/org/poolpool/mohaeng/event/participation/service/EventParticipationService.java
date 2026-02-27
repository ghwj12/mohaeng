package org.poolpool.mohaeng.event.participation.service;

import java.util.List;

import org.poolpool.mohaeng.event.list.dto.EventDetailDto;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventParticipationService {

    List<EventParticipationDto> getParticipationList(Long userId);

    Long submitParticipation(EventParticipationDto dto);

    void cancelParticipation(Long pctId);

    /**
     * ✅ (소프트삭제) 참여내역 삭제 처리
     * - DB row는 유지하고 상태를 '참여삭제'로 변경
     */
    void deleteParticipation(Long pctId);

    /**
     * ✅ (소프트삭제) 참여내역 삭제 처리(권한 체크용 사용자ID 포함)
     */
    void deleteParticipation(Long pctId, Long userId);

    List<ParticipationBoothDto> getParticipationBoothList(Long userId);

    Long submitBoothApply(Long eventId, ParticipationBoothDto dto, List<MultipartFile> files);

    void cancelBoothParticipation(Long pctBoothId);
    
    EventDetailDto getEventDetail(Long eventId);
    
}

