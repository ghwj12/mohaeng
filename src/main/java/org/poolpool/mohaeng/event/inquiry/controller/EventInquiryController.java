package org.poolpool.mohaeng.event.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.inquiry.dto.EventInquiryDto;
import org.poolpool.mohaeng.event.inquiry.service.EventInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventInquiry")
public class EventInquiryController {

    private final EventInquiryService service;

 // ✅ 문의 목록 조회
    // 예: /api/eventInquiry?eventId=1
    @GetMapping("/list")
    public ResponseEntity<List<EventInquiryDto>> getInquiryList(
    		@RequestParam("eventId") Long eventId) {

        return ResponseEntity.ok(service.getInquiryList(eventId));
    }

    // ✅ 문의 등록
    @PostMapping("/createInquiry")
    public ResponseEntity<?> createInquiry(
            @RequestParam("eventId") Long eventId,
            @RequestBody EventInquiryDto dto) {
    	
    	dto.setEventId(eventId);
        return ResponseEntity.ok(service.createInquiry(dto));
    }

    // ✅ 문의 수정
    @PutMapping("/updateInquiry")
    public ResponseEntity<Void> updateInquiry(
    		@RequestParam("inqId") Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.updateInquiry(dto);
        return ResponseEntity.ok().build();
    }

    // ✅ 문의 삭제
    @DeleteMapping("/deleteInquiry")
    public ResponseEntity<Void> deleteInquiry(
    		@RequestParam("inqId") Long inqId) {

        service.deleteInquiry(inqId);
        return ResponseEntity.ok().build();
    }

    // ✅ 답변 등록
    @PostMapping("/createReply")
    public ResponseEntity<Void> createReply(
    		@RequestParam("inqId") Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.createReply(dto);
        return ResponseEntity.ok().build();
    }

    // ✅ 답변 수정
    @PutMapping("/updateReply")
    public ResponseEntity<Void> updateReply(
    		@RequestParam("inqId") Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.updateReply(dto);
        return ResponseEntity.ok().build();
    }

    // ✅ 답변 삭제
    @DeleteMapping("/deleteReply")
    public ResponseEntity<Void> deleteReply(
    		@RequestParam("inqId") Long inqId) {

        service.deleteReply(inqId);
        return ResponseEntity.ok().build();
    }
}

