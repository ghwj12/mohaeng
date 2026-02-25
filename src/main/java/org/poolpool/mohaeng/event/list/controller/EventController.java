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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
			@PageableDefault(size = 12) Pageable pageable) {
		// 서비스 인터페이스 규격에 맞춰 개별 인자로 전달
		Page<EventDto> result = eventService.searchEvents(keyword, regionId, filterStart, filterEnd, categoryId, topicIds,
				checkFree, hideClosed, pageable);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<EventDetailDto> getEventDetail(
	    @PathVariable("eventId") Long eventId,
	    @CookieValue(name = "viewedEvents", required = false) String viewedEvents // 💡 쿠키를 어노테이션으로 바로 가져옵니다.
	) {
	    // 1. 이미 본 이벤트인지 확인
	    boolean isViewed = (viewedEvents != null && viewedEvents.contains("[" + eventId + "]"));

	    // 2. 서비스 호출 (조회수 증가 여부 전달)
	    EventDetailDto detail = eventService.getEventDetail(eventId, !isViewed);

	    // 3. 처음 보는 이벤트라면 쿠키를 포함해서 응답
	    if (!isViewed) {
	        String newValue = (viewedEvents == null ? "" : viewedEvents) + "[" + eventId + "]";
	        
	        ResponseCookie cookie = ResponseCookie.from("viewedEvents", newValue)
	                .path("/")
	                .maxAge(60 * 60 * 24)
	                .httpOnly(true)
	                .secure(false) // HTTPS 환경이라면 true로 변경
	                .build();

	        return ResponseEntity.ok()
	                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // 💡 헤더에 쿠키를 실어 보냅니다.
	                .body(detail);
	    }

	    // 4. 이미 본 이벤트라면 그냥 데이터만 응답
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