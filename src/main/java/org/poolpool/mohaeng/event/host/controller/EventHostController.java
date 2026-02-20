package org.poolpool.mohaeng.event.host.controller;

import java.util.List;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.service.EventHostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventHostController {

    private final EventHostService eventHostService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createEvent(
            @RequestPart("eventData") EventCreateDto createDto, // JSON 데이터
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail, // 썸네일 1개
            @RequestPart(value = "detailFiles", required = false) List<MultipartFile> detailFiles, // 상세 이미지 여러 개
            @RequestPart(value = "boothFiles", required = false) List<MultipartFile> boothFiles // 부스 파일 여러 개
    ) {
        // 서비스 메서드에 파일들도 같이 넘겨줍니다! (서비스 인터페이스도 파라미터 추가 필요)
        Long newEventId = eventHostService.createEventWithDetails(createDto, thumbnail, detailFiles, boothFiles);
        return ResponseEntity.ok(newEventId);
    }
    
    // @PathVariable에 이름 명시 ("eventId") 잊지 마세요!
    @PutMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventHostService.deleteEvent(eventId);
        return ResponseEntity.ok("행사 상태가 DELETED로 성공적으로 변경되었습니다.");
    }
    
}