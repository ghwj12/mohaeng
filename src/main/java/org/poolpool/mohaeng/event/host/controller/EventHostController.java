package org.poolpool.mohaeng.event.host.controller;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.service.EventHostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventHostController {

    private final EventHostService eventHostService;

    @PostMapping
    public ResponseEntity<Long> createEvent(@RequestBody EventCreateDto createDto) {
        // 한 페이지에서 담아온 3개의 DTO 뭉치를 한 방에 처리!
        Long newEventId = eventHostService.createEventWithDetails(createDto);
        return ResponseEntity.ok(newEventId);
    }
    
    // @PathVariable에 이름 명시 ("eventId") 잊지 마세요!
    @PutMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventHostService.deleteEvent(eventId);
        return ResponseEntity.ok("행사 상태가 DELETED로 성공적으로 변경되었습니다.");
    }
    
}