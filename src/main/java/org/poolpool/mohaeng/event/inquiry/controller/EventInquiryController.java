package org.poolpool.mohaeng.event.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.inquiry.dto.EventInquiryDto;
import org.poolpool.mohaeng.event.inquiry.service.EventInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/inquiries")
public class EventInquiryController {

    private final EventInquiryService service;

    // 문의 목록 조회
    @GetMapping
    public ResponseEntity<List<EventInquiryDto>> getInquiryList(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(service.getInquiryList(eventId));
    }

    // 문의 등록
    @PostMapping
    public ResponseEntity<Long> createInquiry(
            @PathVariable Long eventId,
            @RequestBody EventInquiryDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.createInquiry(dto));
    }

    // 문의 수정
    @PutMapping("/{inqId}")
    public ResponseEntity<Void> updateInquiry(
            @PathVariable Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.updateInquiry(dto);
        return ResponseEntity.ok().build();
    }

    // 문의 삭제
    @DeleteMapping("/{inqId}")
    public ResponseEntity<Void> deleteInquiry(
            @PathVariable Long inqId) {

        service.deleteInquiry(inqId);
        return ResponseEntity.ok().build();
    }

    // 답변 등록 (관리자)
    @PostMapping("/{inqId}/reply")
    public ResponseEntity<Void> createReply(
            @PathVariable Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.createReply(dto);
        return ResponseEntity.ok().build();
    }

    // 답변 수정
    @PutMapping("/{inqId}/reply")
    public ResponseEntity<Void> updateReply(
            @PathVariable Long inqId,
            @RequestBody EventInquiryDto dto) {

        dto.setInqId(inqId);
        service.updateReply(dto);
        return ResponseEntity.ok().build();
    }

    // 답변 삭제
    @DeleteMapping("/{inqId}/reply")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long inqId) {

        service.deleteReply(inqId);
        return ResponseEntity.ok().build();
    }
}

