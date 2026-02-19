package org.poolpool.mohaeng.event.inquiry.service;

import org.poolpool.mohaeng.event.inquiry.dto.EventInquiryDto;
import java.util.List;

public interface EventInquiryService {

    List<EventInquiryDto> getInquiryList(Long eventId);

    Long createInquiry(EventInquiryDto dto);

    void updateInquiry(EventInquiryDto dto);

    void deleteInquiry(Long inqId);

    void createReply(EventInquiryDto dto);

    void updateReply(EventInquiryDto dto);

    void deleteReply(Long inqId);
}


