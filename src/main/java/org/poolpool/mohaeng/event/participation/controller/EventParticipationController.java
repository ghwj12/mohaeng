package org.poolpool.mohaeng.event.participation.controller;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.participation.dto.EventParticipationDto;
import org.poolpool.mohaeng.event.participation.dto.ParticipationBoothDto;
import org.poolpool.mohaeng.event.participation.service.EventParticipationService;
import org.springframework.http.MediaType; // 💡 임포트 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 💡 임포트 추가

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
    
    // 이벤트 정보 불러오기
    @GetMapping("/info/{eventId}")
    public ResponseEntity<?> getEventInfo(@PathVariable("eventId") Long eventId) {
        // 500 에러 방지를 위해 데이터가 없는 경우를 체크해주면 좋습니다.
        Object detail = service.getEventDetail(eventId);
        if (detail == null) {
            return ResponseEntity.status(404).body("행사 정보를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(detail);
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

    // 💡 [추가됨] 행사 부스 신청 임시저장
    @PostMapping(value = "/saveBoothApplyTemp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> saveBoothApplyTemp(
            @RequestParam("eventId") Long eventId,
            @RequestPart("data") ParticipationBoothDto dto, // JSON 폼 데이터
            @RequestPart(value = "files", required = false) List<MultipartFile> files) { // 파일 데이터

        return ResponseEntity.ok(service.saveBoothApplyTemp(eventId, dto, files));
    }

    // 💡 [수정됨] 행사 부스 신청 제출(최종) - 파일 업로드 가능하게 변경
    @PostMapping(value = "/submitBoothApply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> submitBoothApply(
            @RequestParam("eventId") Long eventId,
            @RequestPart("data") ParticipationBoothDto dto, // JSON 폼 데이터
            @RequestPart(value = "files", required = false) List<MultipartFile> files) { // 파일 데이터

        return ResponseEntity.ok(service.submitBoothApply(eventId, dto, files));
    }

    // 행사 부스 참여 취소
    @DeleteMapping("/cancelBoothParticipation")
    public ResponseEntity<Void> cancelBoothParticipation(
            @RequestParam("pctBoothId") Long pctBoothId) {

        service.cancelBoothParticipation(pctBoothId);
        return ResponseEntity.ok().build();
    }
}
