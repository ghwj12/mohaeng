package org.poolpool.mohaeng.event.host.controller;

import java.util.List;

import org.poolpool.mohaeng.event.host.dto.EventCreateDto;
import org.poolpool.mohaeng.event.host.service.EventHostService;
import org.poolpool.mohaeng.user.entity.UserEntity; // 본인의 User 엔티티나 Details 클래스 임포트
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
            // 💡 @AuthenticationPrincipal를 통해 토큰에 담긴 유저 정보를 가져옵니다.
            @AuthenticationPrincipal Object userDetails, 
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "detailFiles", required = false) List<MultipartFile> detailFiles,
            @RequestPart(value = "boothFiles", required = false) List<MultipartFile> boothFiles
    ) {
        // 💡 1. userDetails에서 ID 추출 (본인 프로젝트의 메서드에 맞게 수정하세요)
        // 예: ((CustomUserDetails)userDetails).getUserId();
        // 일단 테스트를 위해 11L을 직접 넣거나, 형변환 로직을 넣으시면 됩니다.
        Long hostId = 11L; 
        
        // 💡 2. 서비스 호출 시 hostId를 꼭 같이 넘겨줍니다!
        Long newEventId = eventHostService.createEventWithDetails(createDto, hostId, thumbnail, detailFiles, boothFiles);
        
        return ResponseEntity.ok(newEventId);
    }
    
    @PutMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventHostService.deleteEvent(eventId);
        return ResponseEntity.ok("행사 상태가 DELETED로 성공적으로 변경되었습니다.");
    }
}