package org.poolpool.mohaeng.event.participation.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.poolpool.mohaeng.event.participation.service.EventParticipationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventParticipation")
public class EventParticipationController {

    private final EventParticipationService service;

    // 참여 행사 목록 조회
    @GetMapping("/getParticipationList")
    public List<EventParticipationDto> getParticipationList(@RequestParam Long userId) {
        return service.getParticipationList(userId);
    }

    // 행사 신청 임시 저장
    @PostMapping("/saveParticipationTemp")
    public Long saveParticipationTemp(@RequestBody EventParticipationDto dto) {
        return service.saveParticipationTemp(dto);
    }

    // 행사 신청 제출
    @PostMapping("/submitParticipation")
    public Long submitParticipation(@RequestBody EventParticipationDto dto) {
        return service.submitParticipation(dto);
    }

    // 참여 취소
    @DeleteMapping("/cancelParticipation")
    public void cancelParticipation(@RequestParam Long pctId) {
        service.cancelParticipation(pctId);
    }

    // 참여 행사 부스 목록 조회
    @GetMapping("/getParticipationBoothList")
    public List<ParticipationBoothDto> getParticipationBoothList(@RequestParam Long userId) {
        return service.getParticipationBoothList(userId);
    }

    // 행사 부스 신청 임시 저장
    @PostMapping("/saveBoothApplyTemp")
    public Long saveBoothApplyTemp(@RequestBody ParticipationBoothDto dto) {
        return service.saveBoothApplyTemp(dto);
    }

    // 행사 부스 신청 제출
    @PostMapping("/submitBoothApply")
    public Long submitBoothApply(@RequestBody ParticipationBoothDto dto) {
        return service.submitBoothApply(dto);
    }

    // 행사 부스 참여 취소
    @DeleteMapping("/cancelBoothParticipation")
    public void cancelBoothParticipation(@RequestParam Long pctBoothId) {
        service.cancelBoothParticipation(pctBoothId);
    }
}

