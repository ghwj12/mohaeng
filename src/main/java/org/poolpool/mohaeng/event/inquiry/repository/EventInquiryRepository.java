package org.poolpool.mohaeng.event.inquiry.repository;

import org.poolpool.mohaeng.event.inquiry.entity.EventInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventInquiryRepository extends JpaRepository<EventInquiryEntity, Long> {

    List<EventInquiryEntity> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
