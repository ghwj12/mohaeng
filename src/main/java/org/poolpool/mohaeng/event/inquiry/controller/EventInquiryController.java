package org.poolpool.mohaeng.event.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.inquiry.dto.EventInquiryDto;
import org.poolpool.mohaeng.event.inquiry.service.EventInquiryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventInquiry")
public class EventInquiryController {

    private final EventInquiryService service;

    // 문의 목록
    @GetMapping("/list")
    public List<EventInquiryDto> getInquiryList(@RequestParam Long eventId) {
        return service.getInquiryList(eventId);
    }

    // 문의 등록
    @PostMapping("/createInquiry")
    public Long createInquiry(@RequestBody EventInquiryDto dto) {
        return service.createInquiry(dto);
    }

    // 문의 수정
    @PutMapping("/updateInquiry")
    public void updateInquiry(@RequestBody EventInquiryDto dto) {
        service.updateInquiry(dto);
    }

    // 문의 삭제
    @DeleteMapping("/deleteInquiry")
    public void deleteInquiry(@RequestParam Long inqId) {
        service.deleteInquiry(inqId);
    }

    // 답변 등록
    @PostMapping("/createReply")
    public void createReply(@RequestBody EventInquiryDto dto) {
        service.createReply(dto);
    }

    // 답변 수정
    @PutMapping("/updateReply")
    public void updateReply(@RequestBody EventInquiryDto dto) {
        service.updateReply(dto);
    }

    // 답변 삭제
    @DeleteMapping("/deleteReply")
    public void deleteReply(@RequestParam Long inqId) {
        service.deleteReply(inqId);
    }
}
