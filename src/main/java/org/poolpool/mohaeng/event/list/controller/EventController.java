package org.poolpool.mohaeng.event.list.controller;

import java.time.LocalDate;
import java.util.List;

import org.poolpool.mohaeng.event.list.dto.EventDailyCountDto;
import org.poolpool.mohaeng.event.list.dto.EventDetailDto;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.dto.EventRegionCountDto;
import org.poolpool.mohaeng.event.list.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@GetMapping("/search")
	public ResponseEntity<Page<EventDto>> searchEvents(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "regionId", required = false) Long regionId,
			@RequestParam(name = "filterStart", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterStart,
			@RequestParam(name = "filterEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterEnd,
			@RequestParam(name = "categoryId", required = false) Integer categoryId,
			@RequestParam(name = "topicIds", required = false) List<String> topicIds,
			@RequestParam(name = "checkFree", defaultValue = "false") boolean checkFree,
			@RequestParam(name = "hideClosed", defaultValue = "false") boolean hideClosed,
			@PageableDefault(size = 10) Pageable pageable) {
		// 서비스 인터페이스 규격에 맞춰 개별 인자로 전달
		Page<EventDto> result = eventService.searchEvents(keyword, regionId, filterStart, filterEnd, categoryId, topicIds,
				checkFree, hideClosed, pageable);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<EventDetailDto> getEventDetail(@PathVariable("eventId") Long eventId) {
		// 조회수 증가 로직을 여기에 추가하거나 Service에 포함시키면 좋습니다!
		EventDetailDto detail = eventService.getEventDetail(eventId);
		return ResponseEntity.ok(detail);
	}

	// 지도에서 지역별 마커 숫자를 표시하기 위한 API
	@GetMapping("/counts")
	public ResponseEntity<List<EventRegionCountDto>> getEventCountsByRegion() {
		return ResponseEntity.ok(eventService.getEventCountsByRegion());
	}

	// 달력 전용 지역별 날짜별 행사 개수 조회
	@GetMapping("/calendar-counts")
	public ResponseEntity<List<EventDailyCountDto>> getDailyEventCountsByRegion(
			@RequestParam("regionId") Long regionId) {
		return ResponseEntity.ok(eventService.getDailyEventCountsByRegion(regionId));
	}

}