package org.poolpool.mohaeng.event.host.controller;

import java.util.List;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.service.EventHostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventHostController {

    private final EventHostService eventHostService;
    private final ObjectMapper objectMapper;

    /**
     * 모든 파라미터를 @RequestParam으로 통일
     */
    @PostMapping
    public ResponseEntity<Long> createEvent(
            @RequestPart("eventData") String eventDataJson,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "detailFiles", required = false) List<MultipartFile> detailFiles,
            @RequestParam(value = "boothFiles", required = false) List<MultipartFile> boothFiles
    ) {
        try {
            // 1. JSON 문자열을 DTO로 수동 변환 (ObjectMapper 사용)
            EventCreateDto createDto = objectMapper.readValue(eventDataJson, EventCreateDto.class);

            // 2. 서비스 로직 호출
            Long newEventId = eventHostService.createEventWithDetails(createDto, thumbnail, detailFiles, boothFiles);
            
            return ResponseEntity.ok(newEventId);

        } catch (Exception e) {
            // 에러 발생 시 콘솔에 구체적인 이유 출력
            e.printStackTrace(); 
            throw new RuntimeException("데이터 처리 중 에러 발생: " + e.getMessage());
        }
    }
    
    @PutMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventHostService.deleteEvent(eventId);
        return ResponseEntity.ok("행사 상태가 DELETED로 성공적으로 변경되었습니다.");
    }
}