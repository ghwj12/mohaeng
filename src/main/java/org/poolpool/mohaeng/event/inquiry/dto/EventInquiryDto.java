package org.poolpool.mohaeng.event.inquiry.dto;

import org.poolpool.mohaeng.event.inquiry.entity.EventInquiryEntity;
import java.time.LocalDateTime;

public class EventInquiryDto {

    private Long inqId;
    private Long eventId;
    private Long userId;
    // ✅ 질문 작성자 이름(USERS.NAME)
    private String userName;
    private String content;

    private String replyContent;
    private Long replyId;
    private LocalDateTime replyDate;

    private String status;
    private LocalDateTime createdAt;

    // Entity -> DTO
    public static EventInquiryDto fromEntity(EventInquiryEntity e) {
        EventInquiryDto d = new EventInquiryDto();
        d.inqId = e.getInqId();
        d.eventId = e.getEventId();
        d.userId = e.getUserId();
        // userName은 join/projection에서 채우는 것을 권장(엔티티는 userId만 보유)
        d.content = e.getContent();
        d.replyContent = e.getReplyContent();
        d.replyId = e.getReplyId();
        d.replyDate = e.getReplyDate();
        d.status = e.getStatus();
        d.createdAt = e.getCreatedAt();
        return d;
    }

    // DTO -> Entity (등록용 기본 필드만)
    public EventInquiryEntity toEntity() {
        EventInquiryEntity e = new EventInquiryEntity();
        e.setInqId(this.inqId); // 수정 시 사용, 등록 시 null이면 auto-increment
        e.setEventId(this.eventId);
        e.setUserId(this.userId);
        e.setContent(this.content);
        return e;
    }

    // ===== Getter / Setter =====
    public Long getInqId() { return inqId; }
    public void setInqId(Long inqId) { this.inqId = inqId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Long getReplyId() { return replyId; }
    public void setReplyId(Long replyId) { this.replyId = replyId; }

    public LocalDateTime getReplyDate() { return replyDate; }
    public void setReplyDate(LocalDateTime replyDate) { this.replyDate = replyDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
