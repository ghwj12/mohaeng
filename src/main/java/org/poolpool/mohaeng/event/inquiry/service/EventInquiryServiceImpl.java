package org.poolpool.mohaeng.event.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.poolpool.mohaeng.event.inquiry.dto.EventInquiryDto;
import org.poolpool.mohaeng.event.inquiry.entity.EventInquiryEntity;
import org.poolpool.mohaeng.event.inquiry.repository.EventInquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventInquiryServiceImpl implements EventInquiryService {

    private final EventInquiryRepository repo;

    @Override
    @Transactional(readOnly = true)
    public List<EventInquiryDto> getInquiryList(Long eventId) {
        return repo.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(EventInquiryDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Long createInquiry(EventInquiryDto dto) {
        EventInquiryEntity saved = repo.save(dto.toEntity());
        return saved.getInqId();
    }

    @Override
    @Transactional
    public void updateInquiry(EventInquiryDto dto) {
        EventInquiryEntity e = repo.findById(dto.getInqId())
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        e.setContent(dto.getContent());
        repo.save(e);
    }

    @Override
    @Transactional
    public void deleteInquiry(Long inqId) {
        repo.deleteById(inqId);
    }

    @Override
    @Transactional
    public void createReply(EventInquiryDto dto) {
        EventInquiryEntity e = repo.findById(dto.getInqId())
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        e.setReplyContent(dto.getReplyContent());
        e.setReplyId(dto.getReplyId());
        e.setReplyDate(LocalDateTime.now());
        e.setStatus("완료");

        repo.save(e);
    }

    @Override
    @Transactional
    public void updateReply(EventInquiryDto dto) {
        // 로직 동일(답변 내용 덮어쓰기)
        createReply(dto);
    }

    @Override
    @Transactional
    public void deleteReply(Long inqId) {
        EventInquiryEntity e = repo.findById(inqId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        e.setReplyContent(null);
        e.setReplyId(null);
        e.setReplyDate(null);
        e.setStatus("대기");

        repo.save(e);
    }
}
