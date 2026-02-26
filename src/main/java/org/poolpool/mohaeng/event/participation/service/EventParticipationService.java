package org.poolpool.mohaeng.event.participation.service;

import java.util.List;

import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventParticipationService {

    List<EventParticipationDto> getParticipationList(Long userId);

    Long saveParticipationTemp(EventParticipationDto dto);

    Long submitParticipation(EventParticipationDto dto);

    void cancelParticipation(Long pctId);

    List<ParticipationBoothDto> getParticipationBoothList(Long userId);

    Long saveBoothApplyTemp(Long eventId, ParticipationBoothDto dto, List<MultipartFile> files);

    Long submitBoothApply(Long eventId, ParticipationBoothDto dto, List<MultipartFile> files);

    void cancelBoothParticipation(Long pctBoothId);
}

