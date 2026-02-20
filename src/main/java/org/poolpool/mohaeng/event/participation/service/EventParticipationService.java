package org.poolpool.mohaeng.event.participation.service;

import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;

import java.util.List;

public interface EventParticipationService {

    List<EventParticipationDto> getParticipationList(Long userId);

    Long saveParticipationTemp(EventParticipationDto dto);

    Long submitParticipation(EventParticipationDto dto);

    void cancelParticipation(Long pctId);

    List<ParticipationBoothDto> getParticipationBoothList(Long userId);

    Long saveBoothApplyTemp(Long eventId, ParticipationBoothDto dto);

    Long submitBoothApply(Long eventId, ParticipationBoothDto dto);

    void cancelBoothParticipation(Long pctBoothId);
}

