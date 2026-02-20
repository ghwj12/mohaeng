package org.poolpool.mohaeng.event.host.service;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;

public interface EventHostService {
    Long createEventWithDetails(EventCreateDto createDto);
    void deleteEvent(Long eventId);
}