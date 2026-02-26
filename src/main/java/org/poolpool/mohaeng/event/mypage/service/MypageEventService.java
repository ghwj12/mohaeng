package org.poolpool.mohaeng.event.mypage.service;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.list.dto.EventDto;
import org.poolpool.mohaeng.event.list.repository.EventRepository;
import org.poolpool.mohaeng.event.mypage.dto.MyEventsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageEventService {

    private final EventRepository eventRepository;

    /**
     * ✅ 내가 등록(주최)한 행사 목록
     */
    public MyEventsResponse getMyCreatedEvents(Long userId, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<org.poolpool.mohaeng.event.list.entity.EventEntity> p =
                eventRepository.findByHost_UserIdAndEventStatusNot(userId, "DELETED", pr);

        List<EventDto> items = p.getContent().stream().map(EventDto::fromEntity).toList();

        return MyEventsResponse.builder()
                .items(items)
                .page(page)
                .size(size)
                .totalPages(p.getTotalPages())
                .totalElements(p.getTotalElements())
                .build();
    }
}
