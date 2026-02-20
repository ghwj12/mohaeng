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
@RequestMapping("/api")
public class EventParticipationController {

    private final EventParticipationService service;

    // =========================
    // Participation (행사 참여)
    // =========================

    // 참여 행사 목록 조회 (유저 기준)
    // GET /api/users/{userId}/participations
    @GetMapping("/users/{userId}/participations")
    public ResponseEntity<List<EventParticipationDto>> getParticipationList(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getParticipationList(userId));
    }

    // 행사 신청 임시 저장
    // POST /api/events/{eventId}/participations/temp
    @PostMapping("/events/{eventId}/participations/temp")
    public ResponseEntity<Long> saveParticipationTemp(
            @PathVariable Long eventId,
            @RequestBody EventParticipationDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.saveParticipationTemp(dto));
    }

    // 행사 신청 제출(최종)
    // POST /api/events/{eventId}/participations
    @PostMapping("/events/{eventId}/participations")
    public ResponseEntity<Long> submitParticipation(
            @PathVariable Long eventId,
            @RequestBody EventParticipationDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.submitParticipation(dto));
    }

    // 참여 취소
    // DELETE /api/participations/{pctId}
    @DeleteMapping("/participations/{pctId}")
    public ResponseEntity<Void> cancelParticipation(
            @PathVariable Long pctId) {

        service.cancelParticipation(pctId);
        return ResponseEntity.ok().build();
    }


    // =========================
    // Booth Participation (부스 신청/참여)
    // =========================

    // 유저 기준 부스 참여 목록 조회
    // GET /api/users/{userId}/booth-participations
    @GetMapping("/users/{userId}/booth-participations")
    public ResponseEntity<List<ParticipationBoothDto>> getParticipationBoothList(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getParticipationBoothList(userId));
    }

    // 행사 부스 신청 임시 저장
    // POST /api/events/{eventId}/booth-participations/temp
    @PostMapping("/events/{eventId}/booth-participations/temp")
    public ResponseEntity<Long> saveBoothApplyTemp(
            @PathVariable Long eventId,
            @RequestBody ParticipationBoothDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.saveBoothApplyTemp(dto));
    }

    // 행사 부스 신청 제출(최종)
    // POST /api/events/{eventId}/booth-participations
    @PostMapping("/events/{eventId}/booth-participations")
    public ResponseEntity<Long> submitBoothApply(
            @PathVariable Long eventId,
            @RequestBody ParticipationBoothDto dto) {

        dto.setEventId(eventId);
        return ResponseEntity.ok(service.submitBoothApply(dto));
    }

    // 행사 부스 참여 취소
    // DELETE /api/booth-participations/{pctBoothId}
    @DeleteMapping("/booth-participations/{pctBoothId}")
    public ResponseEntity<Void> cancelBoothParticipation(
            @PathVariable Long pctBoothId) {

        service.cancelBoothParticipation(pctBoothId);
        return ResponseEntity.ok().build();
    }
}

