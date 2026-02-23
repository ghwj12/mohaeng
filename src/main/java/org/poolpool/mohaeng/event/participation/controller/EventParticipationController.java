package org.poolpool.mohaeng.event.participation.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.poolpool.mohaeng.event.participation.service.EventParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventParticipation")
public class EventParticipationController {

    private final EventParticipationService service;

    // =========================
    // Participation (행사 참여)
    // =========================

    // 참여 행사 목록 조회 (유저 기준)
    @GetMapping("/getParticipationList")
    public ResponseEntity<List<EventParticipationDto>> getParticipationList(
    		@RequestParam("userId") Long userId) {

        return ResponseEntity.ok(service.getParticipationList(userId));
    }

    // 행사 신청 제출(최종)
    @PostMapping("/submitParticipation")
    public ResponseEntity<Long> submitParticipation(
    		@RequestParam("eventId") Long eventId,
            @RequestBody EventParticipationDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.submitParticipation(dto));
    }

    // 참여 취소
    @DeleteMapping("/cancelParticipation")
    public ResponseEntity<Void> cancelParticipation(
    		@RequestParam("pctId") Long pctId) {

        service.cancelParticipation(pctId);
        return ResponseEntity.ok().build();
    }


    // =========================
    // Booth Participation (부스 신청/참여)
    // =========================

    // 유저 기준 부스 참여 목록 조회
    @GetMapping("/getParticipationBoothList")
    public ResponseEntity<List<ParticipationBoothDto>> getParticipationBoothList(
    		@RequestParam("userId") Long userId) {

        return ResponseEntity.ok(service.getParticipationBoothList(userId));
    }

    // 행사 부스 신청 제출(최종)
    @PostMapping("/submitBoothApply")
    public ResponseEntity<Long> submitBoothApply(
    		@RequestParam("eventId") Long eventId,
            @RequestBody ParticipationBoothDto dto) {

        return ResponseEntity.ok(service.submitBoothApply(eventId, dto));
    }

    // 행사 부스 참여 취소
    @DeleteMapping("/cancelBoothParticipation")
    public ResponseEntity<Void> cancelBoothParticipation(
    		@RequestParam("pctBoothId") Long pctBoothId) {

        service.cancelBoothParticipation(pctBoothId);
        return ResponseEntity.ok().build();
    }
}

