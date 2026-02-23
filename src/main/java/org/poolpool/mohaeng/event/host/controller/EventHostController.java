package org.poolpool.mohaeng.event.host.controller;

import java.util.List;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.service.EventHostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 💡 중요!
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
            @RequestPart("eventData") EventCreateDto createDto,
            // 💡 CustomUserPrincipal 대신 String으로 직접 받습니다!
            // 현재 필터가 String(userId)을 넣어주고 있기 때문에 이렇게 하면 null이 안 나옵니다.
            @AuthenticationPrincipal String userId, 
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "detailFiles", required = false) List<MultipartFile> detailFiles,
            @RequestPart(value = "boothFiles", required = false) List<MultipartFile> boothFiles
    ) {
        // 💡 이미 userId가 "1" 같은 문자열로 들어왔으니 바로 Long으로 변환만 하면 됩니다.
        if (userId == null) {
            throw new RuntimeException("로그인 정보가 없습니다. (토큰 확인 필요)");
        }
        
        Long hostId = Long.parseLong(userId); 
        
        Long newEventId = eventHostService.createEventWithDetails(createDto, hostId, thumbnail, detailFiles, boothFiles);
        
        return ResponseEntity.ok(newEventId);
    }
    
    @PutMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventHostService.deleteEvent(eventId);
        return ResponseEntity.ok("행사 상태가 DELETED로 성공적으로 변경되었습니다.");
    }
}