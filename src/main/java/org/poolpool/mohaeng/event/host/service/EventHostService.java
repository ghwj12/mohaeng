package org.poolpool.mohaeng.event.host.service;

import java.util.List;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventHostService {
	Long createEventWithDetails(EventCreateDto createDto, MultipartFile thumbnail, List<MultipartFile> detailFiles, List<MultipartFile> boothFiles);
    void deleteEvent(Long eventId);
}